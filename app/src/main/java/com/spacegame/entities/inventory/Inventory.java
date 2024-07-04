package com.spacegame.entities.inventory;

import com.spacegame.core.ui.ItemPickupToast;
import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.items.Item;
import com.spacegame.entities.inventory.items.OnDamageTakenItem;
import com.spacegame.entities.inventory.items.OnEnemyHitItem;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

  /** The additional attack speed from items */
  private float attackSpeed = 0f;

  /**
   * Getter for attackSpeed
   *
   * @return
   */
  public float getAttackSpeed() {
    return this.attackSpeed;
  }

  /**
   * Adds onto the attackSpeed
   *
   * @param attackSpeed
   */
  public void addAttackSpeed(float attackSpeed) {
    this.attackSpeed += attackSpeed;
  }

  /**
   * Substract from attackSpeed
   *
   * @param attackSpeed
   */
  public void subAttackSpeed(float attackSpeed) {
    this.attackSpeed -= attackSpeed;
  }

  /** Value for the absolute base speed that accumulates from the items held by this inventory */
  private float speedAbsolute = 0;

  /**
   * Adds to speedAbsolute, called when a {@link
   * com.spacegame.entities.inventory.items.StatIncreaseItem} is added to the inventory
   *
   * @param speed
   */
  public void addSpeedAbsolute(float speed) {
    this.speedAbsolute += speed;
  }

  /**
   * Subtracts from speedAbsolute, called when a {@link
   * com.spacegame.entities.inventory.items.StatIncreaseItem} is removed to the inventory
   *
   * @param speed
   */
  public void subSpeedAbsolute(float speed) {
    this.speedAbsolute -= speed;
  }

  /**
   * Getter for speedAbsolute
   *
   * @return
   */
  public float getSpeedAbsolute() {
    return this.speedAbsolute;
  }

  /**
   * Value for the relative base speed factor that accumulates from the items held by this inventory
   */
  private float speedRelative = 0;

  /**
   * Adds to speedRelative, called when a {@link
   * com.spacegame.entities.inventory.items.StatIncreaseItem} is added to the inventory
   *
   * @param speed
   */
  public void addSpeedRelative(float speed) {
    this.speedRelative += speed;
  }

  /**
   * Subtracts from speedRelaitve, called when a {@link
   * com.spacegame.entities.inventory.items.StatIncreaseItem} is removed to the inventory
   *
   * @param speed
   */
  public void subSpeedRelative(float speed) {
    this.speedRelative -= speed;
  }

  public float getSpeedRelative() {
    return speedRelative;
  }

  private float attackDamageAbsolute = 0;

  public float getAttackDamageAbsolute() {
    return this.attackDamageAbsolute;
  }

  private float attackDamageRelative = 0;

  public float getAttackDamageRelative() {
    return this.attackDamageRelative;
  }

  /** The actor that holds this inventory */
  public Actor actor;

  private List<Item> allItems = new ArrayList<>();

  public List<Item> getAllItems() {
    return new ArrayList<>(this.allItems);
  }

  public Inventory(Actor actor) {
    this.actor = actor;
  }

  public void addItem(Item item) {
    this.allItems.add(item);
    item.onAdd(this);
    ItemPickupToast.create(item);
  }

  public void removeItem(Item item) {}

  /**
   * Will be called every frame For items with a chance of spawning projectiles or something at
   * random with no event triggering them, only random chance
   */
  public void tick(float deltaTime) {
    for (Item item : this.allItems) {
      item.tick(deltaTime);
    }
  }

  /** Callback for when the actor takes damage. Returns the reduced damage number */
  public int onDamageTaken(Actor from) {
    int damage = from.getCollisionDamage();
    for (Item item : this.allItems) {
      if (item instanceof OnDamageTakenItem i) {
        damage = i.onDamageTaken(damage, from);
      }
    }
    // Make sure the damage taken is not negative
    // This would result in a heal
    return Math.max(0, damage);
  }

  /** Callback for when the actor hits an enemy */
  public void onEnemyHit(Actor target) {
    for (Item item : this.allItems) {
      if (item instanceof OnEnemyHitItem i) {
        i.onEnemyHit(target);
      }
    }
  }
}
