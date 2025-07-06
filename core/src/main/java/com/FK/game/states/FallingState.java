package com.FK.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;

public class FallingState implements PlayerState {
    private float fallTime = 0f;
    private static final float AIR_CONTROL = 0.5f;
    private static final float MAX_FALL_SPEED = -450f;
    private static final float FAST_FALL_THRESHOLD = -300f;
    private boolean isFastFalling = false;

    @Override
    public void enter(Player player) {
        player.setCurrentAnimation(player.isFacingRight() ? PlayerAnimationType.FALLING_RIGHT : PlayerAnimationType.FALLING_LEFT);
        fallTime = 0f;
        isFastFalling = false;
    }

    @Override
    public void update(Player player, float delta) {
        if (!isFastFalling) {
            player.getVelocity().y += Player.GRAVITY * delta;
        }
        player.getVelocity().y = Math.max(player.getVelocity().y, MAX_FALL_SPEED);
        player.getBounds().y += player.getVelocity().y * delta;
        if (player.getBounds().y <= Player.FLOOR_Y) {
            player.getBounds().y = Player.FLOOR_Y;
            player.getVelocity().set(0, 0);
            player.getStateMachine().changeState(new IdleState());
            return;
        }
        
        player.getCurrentAnimation().update(delta);
        handleAirControl(player);
    }

    private void handleAirControl(Player player) {
        float controlFactor = isFastFalling ? AIR_CONTROL * 0.3f : AIR_CONTROL;
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.getVelocity().x = -Player.WALK_SPEED * controlFactor;
            player.setFacingRight(false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.getVelocity().x = Player.WALK_SPEED * controlFactor;
            player.setFacingRight(true);
        }
    }

    @Override
    public void handleInput(Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
           // player.setState(new AirAttackState());
            return;
        }
        
         if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !isFastFalling) {
        isFastFalling = true;
        player.getVelocity().y = MAX_FALL_SPEED; 
    }
    }

    @Override
    public void render(Player player, Batch batch) {
        TextureRegion frame = player.getCurrentAnimation().getCurrentFrame();
        float additionalScale = 0f;
        if (isFastFalling) {
            additionalScale = MathUtils.clamp(
                -player.getVelocity().y / FAST_FALL_THRESHOLD * 0.1f, 
                0f, 
                0.15f
            );
        }
        
        batch.draw(frame,
                 player.getBounds().x,
                 player.getBounds().y,
                 player.getBounds().width * 0.5f,
                 player.getBounds().height * 0.8f, 
                 player.getBounds().width,
                 player.getBounds().height,
                 1f + additionalScale,
                 1f - additionalScale,
                 0);
    }

    @Override
    public void exit(Player player) {
        // Efecto de aterrizaje
        if (player.isOnGround()) {
            float impactForce = Math.abs(player.getVelocity().y);
            
            if (impactForce > FAST_FALL_THRESHOLD) {
                //ParticleSystem.spawnLandingDust(player.getBounds());
                //CameraManager.addScreenShake(0.2f);
            }
        }
    }
}