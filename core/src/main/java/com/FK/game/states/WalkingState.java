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
import com.FK.game.sounds.*;


public class WalkingState implements EntityState<Player> {
    private static final float FOOTSTEP_SOUND_DELAY = 0.3f;
    private float footstepTimer = 0f;
    private boolean lastDirectionRight = true;
    private int airConfirmationCount = 0;
    private static final int REQUIRED_CONFIRMATIONS = 5;

    @Override
    public void enter(Player player) {
        updateAnimation(player);
        footstepTimer = 0f;
        lastDirectionRight = player.isMovingRight();
        airConfirmationCount = 0;
    }

   @Override
    public void update(Player player, float delta) {
        handleInput(player);
        player.getCurrentAnimation().update(delta);
        player.getBounds().y += player.getVelocity().y * delta;

        if (!player.isOnPlataform()) {
            player.getVelocity().y += Player.GRAVITY * delta;
            airConfirmationCount++;

            if (airConfirmationCount >= REQUIRED_CONFIRMATIONS) {
                player.getStateMachine().changeState(new FallingState());
                return;
            }
        } else {
            player.getVelocity().y = 0;
            airConfirmationCount = 0;
        }

        if (player.getVelocity().y > 0) {
            player.getStateMachine().changeState(new JumpingState());
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.isOnPlataform()) {
            player.getStateMachine().changeState(new ChargingJumpState());
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            player.getStateMachine().changeState(new AttackingState());
            SoundCache.getInstance().stopLoop(SoundType.WALK);
            return;
        }

        boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (!isMoving) {
            player.getStateMachine().changeState(new IdleState());
            return;
        }

        SoundCache.getInstance().playLoop(SoundType.WALK, 0.4f);
    }


    @Override
    public void handleInput(Player player) {
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        if (movingLeft) {
            player.setMovingRight(false);
            player.getVelocity().x = -Player.WALK_SPEED;
        } 
        else if (movingRight) {
            player.setMovingRight(true);
            player.getVelocity().x = Player.WALK_SPEED;
        }
        else {
            player.getVelocity().x = 0;
        }
        player.setCurrentAnimation(player.isMovingRight() ? 
            PlayerAnimationType.WALK_RIGHT : PlayerAnimationType.WALK_LEFT);
    }

    private void updateAnimation(Player player) {
        player.setCurrentAnimation(
            player.isMovingRight() ? 
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
        SoundCache.getInstance().stopLoop(SoundType.WALK);
    }

    @Override
    public String toString() {
        return "WalkingState";

    }
}
