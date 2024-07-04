package com.spacegame.entities.inventory.items;

import com.spacegame.core.Game;
import com.spacegame.entities.AnimationOptions;
import com.spacegame.entities.Entity;
import com.spacegame.utils.Constants;

public class ShieldEntity extends Entity {

  Shield from;

  public ShieldEntity(Shield from) {
    super(
        Game.game.textureAtlas,
        from.inventory.actor.getX(),
        from.inventory.actor.getY(),
        from.inventory.actor.getWidth() * 1.9f,
        from.inventory.actor.getHeight() * 1.9f,
        new AnimationOptions(.7f, true, Constants.animation_SHIELD, true));
    this.from = from;
  }

  public static ShieldEntity create(Shield from) {
    ShieldEntity shield = new ShieldEntity(from);
    shield.setX(from.inventory.actor.getX());
    shield.setY(from.inventory.actor.getY());
    shield.vbo().setOpacity(.1f);
    shield.setZ(2f);
    Game.game.addEntity(shield);
    return shield;
  }

  @Override
  public void update(float delta) {
    this.setX(from.inventory.actor.getX());
    this.setY(from.inventory.actor.getY());
    if (from.isActive()) this.vbo().setOpacity(0.5f);
    else this.vbo().setOpacity(0f);
    if (this.anim != null) anim.update(delta);
  }
}
