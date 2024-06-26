package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public class Items {

  public enum AllItems {
    LaserCanon,
    JetPack,
    RocketLauncher
  }

  public static Item createItem(AllItems item, Inventory ownerInventory) {
    return switch (item) {
      case JetPack -> new JetPack(ownerInventory);
      case LaserCanon -> new LaserCanon(ownerInventory);
      case RocketLauncher -> new RocketLauncher(ownerInventory);
    };
  }
}
