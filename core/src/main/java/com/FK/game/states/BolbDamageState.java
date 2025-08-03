package com.FK.game.states;

import com.FK.game.animations.EnemyAnimationType;
import com.FK.game.entities.Bolb;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.FK.game.entities.*;

public class BolbDamageState implements EntityState<Bolb> {

    private final boolean attackerLookRight;
    private float knockbackTimer = 0.2f;
    private float knockbackForceX = 0f;
    private float knockbackForceY = 0f;

    public BolbDamageState(Entity entity) {
        this.attackerLookRight = entity.isMovingRight();
        this.knockbackForceX = entity.getKnockbackX();
        this.knockbackForceY = entity.getKnockbackY();
    }

    @Override
    public void enter(Bolb bolb) {
        bolb.decreaseHealth();
        knockbackForceX = knockbackForceX * (attackerLookRight ? 1f : -1f);
        bolb.getVelocity().x = knockbackForceX;
        bolb.getVelocity().y = knockbackForceY;
    }

    @Override
    public void update(Bolb bolb, float delta) {
        knockbackTimer -= delta;
        bolb.getBounds().x += bolb.getVelocity().x * delta;
        bolb.getBounds().y += bolb.getVelocity().y * delta;

        if (!bolb.isOnSolidGround()) {
            bolb.getVelocity().y += bolb.getGravity() * delta;
        } else {
            bolb.getVelocity().y = 0;
        }

        bolb.getCollisionBox().setPosition(
            bolb.getBounds().x + bolb.getCollisionOffsetX(),
            bolb.getBounds().y + bolb.getCollisionOffsetY()
        );

        if (knockbackTimer <= 0f && bolb.isOnSolidGround()) {
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
    public void handleInput(Bolb bolb) {}

    @Override
    public void exit(Bolb bolb) {}
}
