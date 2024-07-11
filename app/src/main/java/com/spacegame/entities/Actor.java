package com.spacegame.entities;

import com.spacegame.entities.inventory.Inventory;
import com.spacegame.graphics.Sprite;
import com.spacegame.graphics.TextureAtlas;
import com.spacegame.sound.SoundEngine;
import com.spacegame.utils.DebugLogger;
import com.spacegame.utils.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Actor extends Entity {

  /** The id for this actor, uniquely identifying the actor */
  private UUID actorId = UUID.randomUUID();

  /** The damage the other actor takes when colliding with this actor */
  public int collisionDamage = 1;

  /** The max health of the actor */
  private int maxHealth = 1;

  /** The current health of the actor */
  private int currentHealth = maxHealth;

  /** The inventory of the actor */
  public Inventory inventory = new Inventory(this);

  /**
   * Wether this entity is collidable. If true, the entity will be checked for collisions with other
   * entities.
   */
  public boolean collidable;

  /** The own collision mask for this entity. Needs to be set, for collision checking */
  public CollisionMask collisionMask = null;

  /**
   * If the entity is currently colliding. Used to check whether the entity was colliding last
   * frame. This way we can determine if a collision is entered or left or if the collision already
   * happened last frame
   */
  boolean colliding = false;

  /**
   * All other collision masks this entity can collide with. Needs to be set for collision checking
   */
  public ArrayList<CollisionMask> collidesWith = new ArrayList<>();

  /**
   * The velocity of the player. This is used to calculate the next position based on where the
   * player is moving. Used for the illusion of movement of the player, while only the actors are
   * moving, based on the player's velocity.
   */
  Vector2D playerVelocity;


  /**
   * Constructor for the Entity class. This constructor initializes a new Entity object by setting
   * its position, size, texture atlas, and sprite. If the texture atlas is not null, it sets the
   * texture atlas and OpenGL texture pointer of the entity. If both the texture atlas and sprite
   * name are not null, it sets the sprite of the entity and updates its auxiliary data.
   *
   * @param textureAtlas The TextureAtlas object to use for the entity. This object contains the
   *     texture atlas used to render the entity's sprite.
   * @param spriteName The name of the sprite to use for the entity. The sprite is retrieved from
   *     the provided texture atlas.
   * @param x The initial x-coordinate of the entity.
   * @param y The initial y-coordinate of the entity.
   * @param width The width of the entity.
   * @param height The height of the entity.
   */
  public Actor(
      TextureAtlas textureAtlas, String spriteName, float x, float y, float width, float height) {
    super(textureAtlas, spriteName, x, y, width, height);
    this.collidable = true;
  }

  public Actor(
      TextureAtlas textureAtlas,
      float x,
      float y,
      float width,
      float height,
      AnimationOptions anim) {
    super(textureAtlas, x, y, width, height, anim);
    this.collidable = true;
  }

  public Actor(
      TextureAtlas textureAtlas, Sprite sprite, float x, float y, float width, float height) {
    super(textureAtlas, sprite, x, y, width, height);
  }

  @Override
  public void update(float delta) {
    if (this.currentHealth <= 0) {
      this.setDiscard(true);
      return;
    }
    super.update(delta);
    this.inventory.tick(delta);
  }

  /**
   * Check if this entity collides with any of the given list
   *
   * @param others
   * @return
   */
  public void collidesWithAny(List<Entity> others) {
    if (!this.collidable) return;
    for (int i = 0; i < others.size(); i++) {
      Entity e = others.get(i);
      if (e == null) break;
      if (e instanceof Actor o) {
        if (this.isColliding(o)) {
          // if (!this.colliding) onCollision(o);
          onCollision(o);
          this.colliding = true;
        }
      }
    }
    if (this.colliding) onCollisionEnd();
    this.colliding = false;
  }

  /**
   * Checks if the entity is colliding with another entity. This method checks for collisions based
   * on the Separating Axis Theorem (SAT), because we have rotations to count for. If the entities
   * are colliding, the method returns true, false otherwise.
   *
   * @param other The other entity to check collision with
   * @return Whether the two Entities are colliding
   */
  public boolean isColliding(Actor other) {
    if (!this.collidable || !other.collidable) return false;
    if (!this.collidesWith.contains(other.collisionMask)) return false;
    Vector2D[] normals = new Vector2D[8];
    // Get all vertex positions (the corners of the quad) from both shapes
    Vector2D[] thisVertices = this.vbo.getVerticesPositions();
    Vector2D[] otherVertices = other.vbo.getVerticesPositions();

    // Find the normals for both shapes (currently only quads)
    for (int i = 0; i < 4; i++) {
      Vector2D thisEdge = thisVertices[i].sub(thisVertices[(i + 1) % 4]);
      normals[i] = new Vector2D(-thisEdge.getY(), thisEdge.getX()).normalized();
      Vector2D otherEdge = otherVertices[i].sub(otherVertices[(i + 1) % 4]);
      normals[i + 4] = new Vector2D(-otherEdge.getY(), otherEdge.getX()).normalized();
    }

    // Check for overlap for each normal
    for (Vector2D normal : normals) {
      // Init min and max for both shapes
      // This will be needed to check for an overlap
      float minThis = normal.scalarProduct(thisVertices[0]);
      float maxThis = minThis;
      float minOther = normal.scalarProduct(otherVertices[0]);
      float maxOther = minOther;

      // Project vertices onto the normals and find the min and max projection
      // Here we project 2D Vectors onto a 1D Line and find the min and max value for each shape
      // With this we can check for gaps on this 1D Line, if there is one on at least one normal,
      // the entities are not colliding
      for (int i = 1; i < 4; i++) {
        float projectionThis = normal.scalarProduct(thisVertices[i]);
        minThis = Math.min(minThis, projectionThis);
        maxThis = Math.max(maxThis, projectionThis);

        float projectionOther = normal.scalarProduct(otherVertices[i]);
        minOther = Math.min(minOther, projectionOther);
        maxOther = Math.max(maxOther, projectionOther);
      }

      // Check for overlap
      // If this is true, then an gap is found and the shapes are not colliding
      if (maxThis < minOther || maxOther < minThis) {
        return false;
      }
    }
    // If there is no gap found for any normal of both shapes
    // Then the shapes have to collide
    return true;
  }

  /**
   * Called when the entity collides with another entity. This method can be overridden by
   * subclasses to implement custom collision behavior.
   */
  public void onCollision(Actor other) {}

  /**
   * Called the first frame the Entity is no longer colliding. Only called when the entity was
   * colliding the frame before
   */
  public void onCollisionEnd() {}

  /**
   * Set the player velocity.
   *
   * @param playerVelocity
   */
  public void setPlayerVelocity(Vector2D playerVelocity) {
    this.playerVelocity = playerVelocity;
  }

  @Override
  public Vector2D getVelocity() {
    if (this.playerVelocity != null) return this.velocity.add(this.playerVelocity.inversed());
    else return new Vector2D(this.velocity);
  }

  @Override
  public float getBaseSpeed() {
    if (this.inventory == null) return this.baseSpeed;
    return (this.inventory.getSpeedAbsolute() + this.baseSpeed)
        * (1 + this.inventory.getSpeedRelative());
  }

  /**
   * Setter for {@link this.maxHealth}. Also sets the current health back to max health
   *
   * @param newMaxHealth
   */
  public void setMaxHealth(int newMaxHealth) {
    this.maxHealth = newMaxHealth;
    this.currentHealth = newMaxHealth;
  }

  public int getMaxHealth() {
    return this.maxHealth;
  }

  public int getCurrentHealth() {
    return this.currentHealth;
  }

  public void takeDamage(Actor from) {
    int damage = inventory.onDamageTaken(from);
    this.currentHealth -= damage;
  }

  public void takeHeal(int heal) {
    // Make sure the current health won't go above max health
    this.maxHealth += Math.min(this.maxHealth, this.currentHealth + heal);
  }

  public void fullHeal() {
    this.currentHealth = this.maxHealth;
  }

  public int getCollisionDamage() {
    return collisionDamage;
  }

  public void onDeath() {
    this.setDiscard(true);
  }

  public UUID getActorId() {
    return this.actorId;
  }
}
