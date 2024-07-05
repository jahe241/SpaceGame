package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper class for creating {@link Item}s
 */
public class Items {

  /**
   * Listing of all items
   */
  public enum AllItems {
    LaserCanon,
    JetPack,
    RocketLauncher,
    LaserBeam,
    Shield
  }

  /**
   * Create the given item in the given inventory
   * @param item
   * @param ownerInventory
   * @return
   */
  public static Item createItem(AllItems item, Inventory ownerInventory) {
    return switch (item) {
      case JetPack -> new JetPack(ownerInventory);
      case LaserCanon -> new LaserCanon(ownerInventory);
      case RocketLauncher -> new RocketLauncher(ownerInventory);
      case LaserBeam -> new LaserBeam(ownerInventory);
      case Shield -> new Shield(ownerInventory);
    };
  }

  /**
   * Picks a random item from all items
   * @return
   */
  public static AllItems pickRandomItem() {
    float rand = ThreadLocalRandom.current().nextFloat();
    int index = (int) Math.floor(rand * AllItems.values().length);
    return AllItems.values()[index];
  }

  /**
   * Create a pickup at the given coords
   * @param x
   * @param y
   */
  public static void createRandomPickupItem(float x, float y) {
    AllItems item = pickRandomItem();
    ItemPickup.create(item, x, y);
  }
}
