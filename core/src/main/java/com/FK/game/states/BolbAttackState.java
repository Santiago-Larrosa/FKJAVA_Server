package com.FK.game.states;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.FK.game.animations.EnemyAnimationType;
import com.FK.game.core.GameContext;
import com.FK.game.entities.Bolb;
import com.FK.game.entities.Player;
import com.FK.game.states.EntityState;

public class BolbAttackState implements EntityState<Bolb> {

    private float attackTimer;
    private static final float ATTACK_DURATION = 0.1f;

    @Override
    public void enter(Bolb bolb) {
        attackTimer = 0f;
        bolb.setAnimation(EnemyAnimationType.BOLB);
        bolb.getVelocity().x = 0;
        bolb.getDamageBox().set(bolb.getX(), bolb.getY(), bolb.getWidth(), bolb.getHeight());
        bolb.setCanAttack(false);
    }

    @Override
    public void update(Bolb bolb, float delta) {
        attackTimer += delta;
        Player player = GameContext.getPlayer();

        if (player != null) {
            if (player.getBounds().x < bolb.getBounds().x) {
                bolb.setMovingRight(false);
            } else {
                bolb.setMovingRight(true);
            }
        }

        if (attackTimer >= ATTACK_DURATION) {
            bolb.getDamageBox().set(0, 0, 0, 0);
            bolb.getStateMachine().changeState(new BolbWalkState());
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
    public void exit(Bolb bolb) {
        bolb.getDamageBox().set(0, 0, 0, 0);
    }

    @Override
    public void handleInput(Bolb bolb) {}
}
