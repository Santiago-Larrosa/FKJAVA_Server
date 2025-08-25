package com.FK.game.states;

import com.FK.game.animations.EnemyAnimationType;
import com.FK.game.entities.Enemy;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.FK.game.entities.Entity;

public class EnemyDamageState implements EntityState<Enemy> {

    private final boolean attackerLookRight;
    private float damage = 0;
    private float knockbackTimer = 0.2f;
    private float knockbackForceX = 0f;
    private float knockbackForceY = 0f;

    public EnemyDamageState(Entity entity) {
        this.attackerLookRight = entity.isMovingRight();
        this.damage = entity.getDamage();
        this.knockbackForceX = entity.getKnockbackX();
        this.knockbackForceY = entity.getKnockbackY();
    }

    @Override
    public void enter(Enemy enemy) {
        enemy.decreaseHealth(this.damage);
        knockbackForceX = knockbackForceX * (attackerLookRight ? 1f : -1f);
        enemy.getVelocity().x = knockbackForceX;
        enemy.getVelocity().y = knockbackForceY;
        
    }

    @Override
    public void update(Enemy enemy, float delta) {
        knockbackTimer -= delta;
        enemy.getBounds().x += enemy.getVelocity().x * delta;
        enemy.getBounds().y += enemy.getVelocity().y * delta;

        if (!enemy.isOnSolidGround()) {
            enemy.getVelocity().y += enemy.getGravity() * delta;
        } else {
            enemy.getVelocity().y = 0;
        }

        enemy.getCollisionBox().setPosition(
            enemy.getBounds().x + enemy.getCollisionBoxOffsetX(),
            enemy.getBounds().y + enemy.getCollisionBoxOffsetY()
        );

        if (knockbackTimer <= 0f && enemy.isOnSolidGround()) {
            enemy.getStateMachine().changeState(enemy.getDefaultState());
        }
    }

    @Override
    public void render(Enemy enemy, Batch batch) {
        if (enemy.getCurrentAnimation() != null && enemy.getCurrentAnimation().getCurrentFrame() != null) {
            batch.draw(enemy.getCurrentAnimation().getCurrentFrame(),
                enemy.getX(), enemy.getY(),
                enemy.getWidth(), enemy.getHeight());
        }
    }

    @Override
    public void handleInput(Enemy enemy) {}

    @Override
    public void exit(Enemy enemy) {}
}