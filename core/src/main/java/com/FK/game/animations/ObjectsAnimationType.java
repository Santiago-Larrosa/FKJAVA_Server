package com.FK.game.animations;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum ObjectsAnimationType implements AnimationType{

    PORTAL_RISING("portal_rising.png", new int[][] {
        {0, 0, 1240, 1450},
        {1240, 0, 1240, 1450},
        {0, 1450, 1240, 1450},
        {1240, 1450, 1240, 1450}
    }, 0.083f),

    PORTAL_LOOP("portal.png", new int[][] {
        {0, 0, 1240, 1450},
        {1240, 0, 1240, 1450},
        {0, 1450, 1240, 1450},
        {1240, 1450, 1240, 1450},
        {0, 2900, 1240, 1450}
    }, 0.083f);

   private final String texturePath;
    private final int[][] frames;
    private final float frameDuration;
    
    ObjectsAnimationType(String texturePath, int[][] frames, float frameDuration) {
        this.texturePath = texturePath;
        this.frames = frames;
        this.frameDuration = frameDuration;
    }

    @Override
    public String getTexturePath() {
        return this.texturePath;
    }
    
    public AnimationHandler create(Texture texture) {
        return new AnimationHandler(texture, frames, frameDuration);
    }
}