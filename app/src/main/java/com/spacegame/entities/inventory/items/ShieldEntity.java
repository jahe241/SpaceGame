package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.Entity;
import com.spacegame.utils.Constants;

public class ShieldEntity extends Entity {

  Shield from;

  public ShieldEntity(Shield from) {
    super(
        Game.game.textureAtlas,
        Constants.BLUE_PROJECTILE,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        from.inventory.actor.getWidth() * 1.5f,
        from.inventory.actor.getHeight() * 1.5f);
    this.from = from;
  }

  public static ShieldEntity create(Shield from) {
    ShieldEntity shield = new ShieldEntity(from);
    shield.setX(from.inventory.actor.getX());
    shield.setY(from.inventory.actor.getY());
    shield.vbo().setOpacity(0.3f);
    shield.setZ(2f);
    Game.game.addEntity(shield);
    return shield;
  }

  @Override
  public void update(float delta) {
    this.setX(from.inventory.actor.getX());
    this.setY(from.inventory.actor.getY());
    if (from.isActive()) this.vbo().setOpacity(0.3f);
    else this.vbo().setOpacity(0f);
  }
}
