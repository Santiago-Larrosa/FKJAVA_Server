package com.FK.game.states;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;

public class JumpingState implements PlayerState {
    private float airTime = 0f;
    private static final float AIR_CONTROL = 0.7f;

    @Override
    public void enter(Player player) {
        player.setCurrentAnimation(player.isFacingRight() ? PlayerAnimationType.JUMPING_RIGHT : PlayerAnimationType.JUMPING_LEFT);
        player.setOnGround(false);
        airTime = 0f;
    }

    @Override
    public void update(Player player, float delta) {
        if (player.getVelocity().y < 0) {
            player.getStateMachine().changeState(new FallingState());
            return; 
        }
        airTime += delta;
        player.getCurrentAnimation().update(delta); 
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.getVelocity().x = -Player.WALK_SPEED * AIR_CONTROL;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.getVelocity().x = Player.WALK_SPEED * AIR_CONTROL;
        }
    }

    @Override
    public void handleInput(Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            //player.setState(new AirAttackState());
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.setFacingRight(false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.setFacingRight(true);
        }
    }

    @Override
    public void render(Player player, Batch batch) {
        TextureRegion frame = player.getCurrentAnimation().getCurrentFrame();
        float scale = 1.0f + (float)Math.sin(airTime * 10) * 0.05f; 
        
        batch.draw(frame,
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
        if (player.getVelocity().y < 0) {
            //ParticleSystem.spawnAirTrail(player.getBounds());
        }
    }
}