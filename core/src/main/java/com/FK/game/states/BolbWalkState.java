package com.FK.game.states;

import com.FK.game.entities.Bolb;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.Player;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BolbWalkState implements EntityState<Bolb> {

    private float waitTimer = 0f;
    private boolean waitingToTurn = false;
    private final float waitDuration = 0.5f;
    private boolean edgeDetected = false;

    @Override
    public void enter(Bolb bolb) {
        bolb.setAnimation(bolb.isMovingRight() ? EnemyAnimationType.BOLB : EnemyAnimationType.BOLB_LEFT);
    }

    @Override
    public void update(Bolb bolb, float delta) {
        bolb.getCurrentAnimation().update(delta);

        Player player = GameContext.getPlayer();

        if (waitingToTurn) {
            waitTimer += delta;
            if (waitTimer >= waitDuration) {
                waitingToTurn = false;
                waitTimer = 0f;
                bolb.setMovingRight(!bolb.isMovingRight());
                edgeDetected = true;

                bolb.setAnimation(bolb.isMovingRight() ? EnemyAnimationType.BOLB : EnemyAnimationType.BOLB_LEFT);
            }
            
            if (!hasGroundAhead(bolb) && !edgeDetected) {
                waitingToTurn = true;
                return;
            }

            if (player != null && bolb.canAttack()) {
                Vector2 bolbPos = new Vector2(bolb.getBounds().x, bolb.getBounds().y);
                Vector2 playerPos = new Vector2(player.getBounds().x, player.getBounds().y);
                float distance = bolbPos.dst(playerPos);
                if (distance < 50f) {
                    bolb.getStateMachine().changeState(new BolbAttackState());
                }
            }


            return;
        }

        bolb.getVelocity().x = bolb.isMovingRight() ? bolb.getSpeed() : -bolb.getSpeed();
        bolb.getBounds().x += bolb.getVelocity().x * delta;

        if (!hasGroundAhead(bolb) && !edgeDetected) {
            waitingToTurn = true;
            return;
        }

        if (hasGroundAhead(bolb)) {
            edgeDetected = false;
        }

        if (bolb.getKnockbackTimer() > 0f) {
            bolb.setKnockbackTimer(bolb.getKnockbackTimer() - delta);
        } else {
            if (!bolb.isOnSolidGround()) {
                bolb.getVelocity().y += bolb.getGravity() * delta;
            } else {
                bolb.getVelocity().y = 0;
            }
        }

        bolb.getBounds().y += bolb.getVelocity().y * delta;
        bolb.getCollisionBox().setPosition(
            bolb.getBounds().x + bolb.getCollisionOffsetX(),
            bolb.getBounds().y + bolb.getCollisionOffsetY()
        );

        if (player != null && bolb.canAttack()) {
            Vector2 bolbPos = new Vector2(bolb.getBounds().x, bolb.getBounds().y);
            Vector2 playerPos = new Vector2(player.getBounds().x, player.getBounds().y);
            float distance = bolbPos.dst(playerPos);
            if (distance < 50f) {
                bolb.getStateMachine().changeState(new BolbAttackState());
            }
        }
    }

    @Override
    public void render(Bolb bolb, Batch batch) {
        if (bolb.getCurrentAnimation() != null && bolb.getCurrentAnimation().getCurrentFrame() != null) {
            batch.draw(bolb.getCurrentAnimation().getCurrentFrame(),
                bolb.getX(), bolb.getY(),
                bolb.getWidth(), bolb.getHeight());
        }
    }

    @Override
    public void handleInput(Bolb bolb) {}

    @Override
    public void exit(Bolb bolb) {}

    private boolean isWallAhead(Bolb bolb) {
        float checkX = bolb.isMovingRight()
            ? bolb.getCollisionBox().x + bolb.getCollisionBox().width + 1
            : bolb.getCollisionBox().x - 1;

        float checkY = bolb.getCollisionBox().y;
        float checkHeight = bolb.getCollisionBox().height;

        Rectangle checkArea = new Rectangle(checkX, checkY, 1, checkHeight);

        for (Rectangle platform : bolb.getCollisionObjects()) {
            if (checkArea.overlaps(platform)) return true;
        }

        return false;
    }


    private boolean hasGroundAhead(Bolb bolb) {
        float checkX = bolb.isMovingRight()
            ? bolb.getCollisionBox().x + bolb.getCollisionBox().width + 5
            : bolb.getCollisionBox().x - 5;

        Rectangle checkArea = new Rectangle(checkX, bolb.getCollisionBox().y - 5, 10, 5);
        for (Rectangle platform : bolb.getCollisionObjects()) {
            if (checkArea.overlaps(platform)) return true;
        }
        return false;
    }
}
