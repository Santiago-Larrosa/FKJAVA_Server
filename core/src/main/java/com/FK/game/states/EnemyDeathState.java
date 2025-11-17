package com.FK.game.states;

import com.badlogic.gdx.Gdx;
import com.FK.game.animations.EnemyAnimationType; 
import com.FK.game.entities.Enemy;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnemyDeathState<E extends Enemy<E>> implements EntityState<E> {

    @Override
    public void enter(E enemy) {
        enemy.getVelocity().set(0, 0);
        enemy.setCurrentAnimation(EnemyAnimationType.SMOKE); 
        enemy.getCurrentAnimation().reset();
    }

    @Override
    public void update(E enemy, float delta) {
        enemy.getCurrentAnimation().update(delta);

        if (enemy.getCurrentAnimation().isFinished()) {
            Gdx.app.log("YA SE PUEDE MORIR", "El enemigo ya se puede morir");
            enemy.setReadyForRemoval(true);
        }
    }

    @Override
    public void render(E enemy, Batch batch) {
        TextureRegion frame = enemy.getCurrentAnimation().getCurrentFrame();
        batch.draw(frame, 
                   enemy.getX(), 
                   enemy.getY(), 
                   enemy.getWidth(), 
                   enemy.getHeight());
    }

    @Override public void exit(E enemy) {}
    @Override public void handleInput(E enemy) {}
}
