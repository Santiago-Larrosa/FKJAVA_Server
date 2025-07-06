package com.FK.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;

public class WalkingState implements PlayerState {
    private static final float FOOTSTEP_SOUND_DELAY = 0.3f;
    private float footstepTimer = 0f;
    private boolean lastDirectionRight = true;

    @Override
    public void enter(Player player) {
        updateAnimation(player);
        footstepTimer = 0f;
        lastDirectionRight = player.isFacingRight();
    }

    @Override
    public void update(Player player, float delta) {
        handleInput(player);
        player.getCurrentAnimation().update(delta);
        boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        footstepTimer += delta;
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.getStateMachine().changeState(new IdleState());
        }
         if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.isOnGround()) {
            player.getStateMachine().changeState(new ChargingJumpState());
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            player.getStateMachine().changeState(new AttackingState());
        }
    }

    @Override
    public void handleInput(Player player) {
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        if (movingLeft) {
            player.setFacingRight(false);
            player.getVelocity().x = -Player.WALK_SPEED;
        } 
        else if (movingRight) {
            player.setFacingRight(true);
            player.getVelocity().x = Player.WALK_SPEED;
        }
        else {
            player.getVelocity().x = 0;
        }
        player.setCurrentAnimation(player.isFacingRight() ? 
            PlayerAnimationType.WALK_RIGHT : PlayerAnimationType.WALK_LEFT);
    }

    private void updateAnimation(Player player) {
        player.setCurrentAnimation(
            player.isFacingRight() ? 
                PlayerAnimationType.WALK_RIGHT : 
                PlayerAnimationType.WALK_LEFT
        );
    }

    @Override
    public void render(Player player, Batch batch) {
        TextureRegion frame = player.getCurrentAnimation().getCurrentFrame();
        float tiltAngle = (float) Math.sin(footstepTimer * 20f) * 3f;
        
        batch.draw(frame,
                 player.getBounds().x,
                 player.getBounds().y,
                 player.getBounds().width * 0.5f,
                 player.getBounds().height * 0.1f,
                 player.getBounds().width,
                 player.getBounds().height,
                 1f, 1f, tiltAngle);
    }

    @Override
    public void exit(Player player) {
        player.getVelocity().x = 0; 
    }

    @Override
    public String toString() {
        return "WalkingState";
    }
}