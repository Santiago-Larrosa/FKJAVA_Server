package com.FK.game.states;

import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.Boss;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.FK.game.network.*;
import com.FK.game.animations.EnemyAnimationType;

public class BossIdleState implements EntityState<Boss> {
    private float attackTimer;
    private static final float ATTACK_INTERVAL = 4f; 

    @Override
    public void enter(Boss enemy) {
        attackTimer = 0f;
        enemy.setAnimation(EnemyAnimationType.BOLB);
        enemy.setStateMessage(StateMessage.BOSS_IDLE);
    }

    @Override
    public void update(Boss enemy, float delta) {
        attackTimer += delta;
        if (attackTimer >= ATTACK_INTERVAL && enemy.isPlayerInRange()) {
            enemy.acquireTarget(); 
            enemy.getStateMachine().changeState(new BossLaserAttackState());
        }
    }

    @Override
    public void render(Boss enemy, com.badlogic.gdx.graphics.g2d.Batch batch) {
    }

    @Override
    public void exit(Boss enemy) {
    }

    @Override
    public void handleInput(Boss enemy) {}
}
