package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public abstract class Item {
  int id;
  String name;
  Inventory inventory;
  ItemClass itemClass;
}
