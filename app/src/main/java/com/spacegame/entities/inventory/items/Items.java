package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;
import java.util.concurrent.ThreadLocalRandom;

public class Items {

  public enum AllItems {
    LaserCanon,
    JetPack,
    RocketLauncher,
    LaserBeam,
    Shield
  }

  public static Item createItem(AllItems item, Inventory ownerInventory) {
    return switch (item) {
      case JetPack -> new JetPack(ownerInventory);
      case LaserCanon -> new LaserCanon(ownerInventory);
      case RocketLauncher -> new RocketLauncher(ownerInventory);
      case LaserBeam -> new LaserBeam(ownerInventory);
      case Shield -> new Shield(ownerInventory);
    };
  }

  public static AllItems pickRandomItem() {
    float rand = ThreadLocalRandom.current().nextFloat();
    int index = (int) Math.floor(rand * AllItems.values().length);
    AllItems ret = AllItems.values()[index];
    return ret;
  }

  public static void createRandomPickupItem(float x, float y) {
    AllItems item = pickRandomItem();
    ItemPickup.create(item, x, y);
  }
}
