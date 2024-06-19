package com.spacegame.entities.inventory;

import com.spacegame.entities.Actor;
import com.spacegame.entities.inventory.items.AtRandomItem;
import com.spacegame.entities.inventory.items.Item;
import com.spacegame.entities.inventory.items.OnDamageTakenItem;
import com.spacegame.entities.inventory.items.OnEnemyHitItem;
import com.spacegame.entities.inventory.items.StatIncreaseItem;
import java.util.ArrayList;
import java.util.List;

public class Inventory {

  /** Value for the absolute base speed that accumulates from the items held by this inventory */
  private float speedAbsolute = 1000;

  public float getSpeedAbsolute() {
    return this.speedAbsolute;
  }

  /**
   * Value for the relative base speed factor that accumulates from the items held by this inventory
   */
  private float speedRelative = 0;

  public float getSpeedRelative() {
    return speedRelative;
  }

  /** The actor that holds this inventory */
  Actor actor;

  /** All items held by this inventory */
  private final List<AtRandomItem> atRandomItems = new ArrayList<>();

  private final List<OnDamageTakenItem> onDamageTakenItems = new ArrayList<>();

  private final List<OnEnemyHitItem> onEnemyHitItems = new ArrayList<>();

  private final List<StatIncreaseItem> statIncreaseItems = new ArrayList<>();

  public Inventory(Actor actor) {
    this.actor = actor;
  }

  public void addItem(Item item) {
    if (item instanceof AtRandomItem randomItem) {
      this.atRandomItems.add(randomItem);
    } else if (item instanceof OnDamageTakenItem dt) {
      this.onDamageTakenItems.add(dt);
    } else if (item instanceof OnEnemyHitItem eh) {
      this.onEnemyHitItems.add(eh);
    } else if (item instanceof StatIncreaseItem si) {
      this.statIncreaseItems.add(si);
      si.calcStatIncrease();
    }
    // TODO: Add item values to
  }

  public void removeItem(Item item) {}

  /**
   * Will be called every frame For items with a chance of spawning projectiles or something at
   * random with no event triggering them, only random chance
   */
  public void tick() {
    for (AtRandomItem item : this.atRandomItems) {
      item.tick();
    }
  }

  /** Callback for when the actor takes damage */
  public void onDamageTaken() {
    for (OnDamageTakenItem item : this.onDamageTakenItems) {
      item.onDamageTaken();
    }
  }

  /** Callback for when the actor hits an enemy */
  public void onEnemyHit() {
    for (OnEnemyHitItem item : this.onEnemyHitItems) {
      item.onEnemyHit();
    }
  }

  /**
   * Retrieves all items this {@link Inventory} is holding
   *
   * @return
   */
  public List<Item> getAllItems() {
    List<Item> allItems = new ArrayList<>();
    allItems.addAll(this.atRandomItems);
    allItems.addAll(this.onDamageTakenItems);
    allItems.addAll(this.onEnemyHitItems);
    allItems.addAll(this.statIncreaseItems);
    return allItems;
  }

  public List<Item> getAtRandomItems() {
    return new ArrayList<>(this.atRandomItems);
  }

  public List<Item> getOnDamageTakenItems() {
    return new ArrayList<>(this.onDamageTakenItems);
  }

  public List<Item> getOnEnemyHitItems() {
    return new ArrayList<>(this.onEnemyHitItems);
  }

  public List<Item> getStatIncreaseItems() {
    return new ArrayList<>(this.statIncreaseItems);
  }
}
