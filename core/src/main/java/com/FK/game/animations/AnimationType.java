package com.FK.game.animations;

import com.badlogic.gdx.graphics.Texture;

public interface AnimationType {
    String getTexturePath();
    AnimationHandler create(Texture texture);
}
