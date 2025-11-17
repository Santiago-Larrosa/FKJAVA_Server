package com.FK.game.states;

import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.Bolb;
import com.FK.game.entities.Player;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.FK.game.network.*;


public class BolbWalkState implements EntityState<Bolb> {

    private float waitTimer = 0f;
    private boolean waitingToTurn = false;
    private final float waitDuration = 0.5f;
    private boolean edgeDetected = false;
    private int[] airConfirmationCount = new int[1];
    private final Vector2 bolbPos = new Vector2();
    private final Vector2 playerPos = new Vector2();


    @Override
    public void enter(Bolb bolb) {
        bolb.setStateMessage(StateMessage.BOLB_WALKING);    
        bolb.setAnimation(bolb.isMovingRight() ? EnemyAnimationType.BOLB : EnemyAnimationType.BOLB_LEFT);
        airConfirmationCount[0] = 0;
    }

    @Override
    public void update(Bolb bolb, float delta) {
        bolb.getCurrentAnimation().update(delta);

        if (waitingToTurn) {
            waitTimer += delta;
            if (waitTimer >= waitDuration) {
                waitingToTurn = false;
                waitTimer = 0f;
                bolb.setMovingRight(!bolb.isMovingRight());
                edgeDetected = true;

                bolb.setAnimation(bolb.isMovingRight() ? EnemyAnimationType.BOLB : EnemyAnimationType.BOLB_LEFT);
            }
            return;
        }

        bolb.getVelocity().x = bolb.isMovingRight() ? bolb.getSpeed() : -bolb.getSpeed();
        bolb.getBounds().x += bolb.getVelocity().x * delta;

        if (!waitingToTurn && (bolb.hasWallAhead() || ( !StateUtils.hasGroundAhead(bolb) && !edgeDetected ))) {
                waitingToTurn = true;
                waitTimer = 0f; 
            }

        if (StateUtils.hasGroundAhead(bolb)) {
            edgeDetected = false;
        }

        if (bolb.getKnockbackTimer() > 0f) {
            bolb.setKnockbackTimer(bolb.getKnockbackTimer() - delta);
        } else {
            if (StateUtils.checkFalling(bolb, delta, airConfirmationCount)) {
            return;
        }
        }

        bolb.getBounds().y += bolb.getVelocity().y * delta;
        bolb.getCollisionBox().setPosition(
            bolb.getBounds().x + bolb.getCollisionBoxOffsetX(),
            bolb.getBounds().y + bolb.getCollisionBoxOffsetY()
        );

        if (bolb.isPlayerInRange() && bolb.canAttack()) {
            bolb.getStateMachine().changeState(new BolbAttackState());
            return;
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
    public void handleInput(Bolb enemy) {}

    @Override
    public void exit(Bolb enemy) {}




}
