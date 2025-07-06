package com.FK.game.animations;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum PlayerAnimationType {
    WALK_RIGHT("walk.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754},
        {0, 3510, 2480, 1754},
        {2481, 3510, 2480, 1754},
        {0, 5265, 2480, 1754}
    }, 0.083f),
    
    WALK_LEFT("walk_left.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754},
        {0, 3510, 2480, 1754},
        {2481, 3510, 2480, 1754},
        {0, 5265, 2480, 1754}
    }, 0.083f),
    
    IDLE_RIGHT("idle.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754}
    }, 0.083f),
    
    IDLE_LEFT("idleLeft.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754}
    }, 0.083f),
    
    JUMPING_RIGHT("jumping.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754}
    }, 0.083f),
    
    JUMPING_LEFT("jumpingLeft.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754}
    }, 0.083f),
    
    CHARGE_JUMP_RIGHT("chargejump.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754}
    }, 0.083f),
    
    CHARGE_JUMP_LEFT("chargeLeftJump.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754}
    }, 0.083f),
    
    FALLING_RIGHT("falling.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754}
    }, 0.083f),
    
    FALLING_LEFT("fallingLeft.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754}
    }, 0.083f),
    
    ATTACKING_RIGHT("attacking.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754},
        {0, 1755, 2480, 1754}
    }, 0.083f),
    
    ATTACKING_LEFT("attackingLeft.png", new int[][] {
        {0, 0, 2480, 1754},
        {0, 1755, 2480, 1754},
        {2481, 0, 2480, 1754},
        {2481, 1755, 2480, 1754},
        {0, 1755, 2480, 1754}
    }, 0.083f);

   private final String texturePath;
    private final int[][] frames;
    private final float frameDuration;
    
    PlayerAnimationType(String texturePath, int[][] frames, float frameDuration) {
        this.texturePath = texturePath;
        this.frames = frames;
        this.frameDuration = frameDuration;
    }

    public String getTexturePath() {
        return this.texturePath;
    }
    
    public AnimationHandler create(Texture texture) {
        return new AnimationHandler(texture, frames, frameDuration);
    }
}