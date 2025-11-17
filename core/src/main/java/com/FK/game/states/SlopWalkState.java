package com.FK.game.states;

import com.badlogic.gdx.Gdx;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.Slop;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.FK.game.network.*;


public class SlopWalkState implements EntityState<Slop> {
    
        private float waitTimer = 0f;
    private boolean waitingToTurn = false;
    private final float waitDuration = 0.5f;
    private boolean edgeDetected = false;
    private int[] airConfirmationCount = new int[1];
    private final Vector2 slopPos = new Vector2();
    private final Vector2 playerPos = new Vector2();

    @Override
    public void enter(Slop slop) {
        slop.setStateMessage(StateMessage.SLOP_WALKING);
        slop.setCurrentAnimation(slop.isMovingRight() ? EnemyAnimationType.SLOP : EnemyAnimationType.SLOP_LEFT);
        airConfirmationCount[0] = 0;
    }

     @Override
    public void update(Slop slop, float delta) {
        slop.getCurrentAnimation().update(delta);
        
        // --- NUEVA LÓGICA DE ATAQUE (SIMPLE Y LIMPIA) ---
        // Primero, comprobamos si debemos atacar.
        if (slop.isPlayerInRange() && slop.canAttack()) {
            slop.getStateMachine().changeState(new SlopAttackState());
            return; // Si atacamos, no necesitamos movernos en este frame.
        }

        // --- LÓGICA DE MOVIMIENTO (SIN CAMBIOS, SOLO REORDENADA) ---
        if (waitingToTurn) {
            waitTimer += delta;
            if (waitTimer >= waitDuration) {
                waitingToTurn = false;
                waitTimer = 0f;
                slop.setMovingRight(!slop.isMovingRight());
                edgeDetected = true;
                slop.setCurrentAnimation(slop.isMovingRight() ? EnemyAnimationType.SLOP : EnemyAnimationType.SLOP_LEFT);
            }
            return; // Mientras espera para girar, no hace nada más.
        }

        slop.getVelocity().x = slop.isMovingRight() ? slop.getSpeed() : -slop.getSpeed();
        slop.getBounds().x += slop.getVelocity().x * delta;

        if (!waitingToTurn && (slop.hasWallAhead() || (!StateUtils.hasGroundAhead(slop) && !edgeDetected))) {
            waitingToTurn = true;
            waitTimer = 0f;
        }

        if (StateUtils.hasGroundAhead(slop)) {
            edgeDetected = false;
        }

        if (StateUtils.checkFalling(slop, delta, airConfirmationCount)) {
            return;
        }

        slop.getBounds().y += slop.getVelocity().y * delta;
        slop.getCollisionBox().setPosition(
            slop.getBounds().x + slop.getCollisionBoxOffsetX(),
            slop.getBounds().y + slop.getCollisionBoxOffsetY()
        );
    }

    @Override
    public void render(Slop slop, Batch batch) {
        if (slop.getCurrentAnimation() != null && slop.getCurrentAnimation().getCurrentFrame() != null) {
            batch.draw(slop.getCurrentAnimation().getCurrentFrame(),
                slop.getX(), slop.getY(),
                slop.getWidth(), slop.getHeight());
        }
    }

    @Override
    public void handleInput(Slop enemy) {}

    @Override
    public void exit(Slop enemy) {}

 


    
}
