package com.FK.game.states;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.FK.game.entities.*;

public class EnemyFallingState<E extends Enemy<E>> implements EntityState<E> {  
    private int groundConfirmationCount = 0;
    private static final int REQUIRED_CONFIRMATIONS = 1;

    @Override
    public void enter(E enemy) {
        groundConfirmationCount = 0;
    }

    @Override
    public void update(E enemy, float delta) {
        enemy.getVelocity().y += enemy.getGravity() * delta;

        float movementY = enemy.getVelocity().y * delta;
        enemy.getBounds().y += movementY;

        if (checkGroundCollision(enemy)) {
            groundConfirmationCount++;
            if (groundConfirmationCount >= REQUIRED_CONFIRMATIONS) {
                handleLanding(enemy);
            }
        } else {
            groundConfirmationCount = 0;
        }

        enemy.setOnGround(false);
        enemy.getCurrentAnimation().update(delta);
    }

    private void handleLanding(E enemy) {
        enemy.getVelocity().y = 0;
        enemy.setOnGround(true);
        if (enemy instanceof Bolb bolb){
            bolb.getStateMachine().changeState(new BolbWalkState());
        } else if (enemy instanceof Slop slop) {
            slop.getStateMachine().changeState(new SlopWalkState());
        }
        
    }

    private boolean checkGroundCollision(E enemy) {
        Rectangle bolbBounds = enemy.getBounds();
        float sensorHeight = 6f;

        Rectangle mainSensor = new Rectangle(
                bolbBounds.x + bolbBounds.width * 0.3f,
                bolbBounds.y - sensorHeight,
                bolbBounds.width * 0.4f,
                sensorHeight
        );

        for (Rectangle platform : enemy.getCollisionObjects()) {
            final float TOLERANCE = 8f;

            if (mainSensor.overlaps(platform) &&
                    enemy.getVelocity().y <= 0 &&
                    enemy.getCollisionBox().y >= platform.y + platform.height - TOLERANCE) {

                enemy.getCollisionBox().y = platform.y + platform.height;
                enemy.getVelocity().y = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleInput(E enemy) {
    }

    @Override
    public void render(E enemy, Batch batch) {
        TextureRegion frame = enemy.getCurrentAnimation().getCurrentFrame();
        batch.draw(frame,
                enemy.getBounds().x,
                enemy.getBounds().y,
                enemy.getBounds().width,
                enemy.getBounds().height);
    }

    @Override
    public void exit(E enemy) {
    }
}
