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


public class IdleState implements EntityState<Player> {
    private float idleTime = 0f;
    private static final float IDLE_ANIMATION_DELAY = 0.2f; 

    @Override
    public void enter(Player player) {
        player.getBounds().y = Player.FLOOR_Y;
        player.setCurrentAnimation(player.isMovingRight() ? 
            PlayerAnimationType.IDLE_RIGHT : PlayerAnimationType.IDLE_LEFT);
    }

    @Override
    public void update(Player player, float delta) {
        idleTime += delta;
        player.getCurrentAnimation().update(delta);

        if (Math.abs(player.getVelocity().x) > 10f) {
            player.getStateMachine().changeState(new WalkingState());
        }
        else if (player.getVelocity().y > 0) {
            player.getStateMachine().changeState(new JumpingState());
        }
        else if (!player.isOnGround()) {
            player.getStateMachine().changeState(new FallingState());
        }
    }


    @Override
    public void handleInput(Player player) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.getStateMachine().changeState(new WalkingState());
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && player.isOnGround()) {
            player.getStateMachine().changeState(new ChargingJumpState());
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            player.getStateMachine().changeState(new AttackingState());
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            player.getStateMachine().changeState(new FireAttackState());
        }

    }

    @Override
    public void render(Player player, Batch batch) {
        TextureRegion currentFrame = player.getCurrentAnimation().getCurrentFrame();
        float scale = 1.0f;
        if (idleTime > 5f) { 
            scale = 1.0f + (float)Math.sin(idleTime * 2) * 0.02f; 
        }
        
        batch.draw(currentFrame, 
                  player.getBounds().x, 
                  player.getBounds().y, 
                  player.getBounds().width * 0.5f, 
                  player.getBounds().height * 0.5f,
                  player.getBounds().width, 
                  player.getBounds().height, 
                  scale, 
                  scale, 
                  0);
    }

    @Override
    public void exit(Player player) {
        idleTime = 0f;
    }

    @Override
    public String toString() {
        return "IdleState";
    }
}