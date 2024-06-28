package com.spacegame.entities.inventory.items;

import com.spacegame.entities.inventory.Inventory;

public class JetPack extends StatIncreaseItem {

  private static final float SPEED_INCREASE = 100f;

  public JetPack(Inventory inventory) {
    super(Items.AllItems.JetPack.ordinal(), "Jet Pack", "Rockets go brrr (+100 speed)", inventory);
  }

  @Override
  public void onAdd(Inventory inventory) {
    inventory.addSpeedAbsolute(SPEED_INCREASE);
  }

  @Override
  public void onRemove(Inventory inventory) {
    inventory.subSpeedAbsolute(SPEED_INCREASE);
  }
}
