package com.FK.game.animations;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum EnemyAnimationType implements AnimationType{

    BOLB("bolb.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754},
        {0, 3510, 2480, 1754},
        {2481, 3510, 2480, 1754},
        {0, 5265, 2480, 1754}
    }, 0.083f),

    BOLB_LEFT("bolbLEFT.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754},
        {0, 3510, 2480, 1754},
        {2481, 3510, 2480, 1754},
        {0, 5265, 2480, 1754}
    }, 0.083f);

   private final String texturePath;
    private final int[][] frames;
    private final float frameDuration;
    
    EnemyAnimationType(String texturePath, int[][] frames, float frameDuration) {
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