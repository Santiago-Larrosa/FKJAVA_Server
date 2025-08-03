package com.FK.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.List;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.FK.game.sounds.*;

import java.util.Random;

public class Bolb extends Entity {

    private AnimationHandler[] animations;
    private EnemyAnimationType currentAnimationType;
    private AnimationHandler currentAnimation;

    private float speed = 100f;
    private float gravity = -900f;
    private float knockbackTimer = 0f;
    private boolean canAttack = true;
    private float attackCooldownTimer = 0f;
    private EntityState<Bolb> currentState;
    private EntityStateMachine<Bolb> stateMachine;

    public Bolb(Array<Rectangle> collisionObjects) {
        super(0, 0, 250, 300, 100, 150);
        setHealth(3);
        setCollisionBoxOffset(100f, 0f);
        this.collisionObjects = collisionObjects;
        animations = new AnimationHandler[EnemyAnimationType.values().length];
        AnimationCache cache = AnimationCache.getInstance();

        for (EnemyAnimationType type : EnemyAnimationType.values()) {
            animations[type.ordinal()] = cache.getAnimation(type);
        }

        setCurrentAnimation(EnemyAnimationType.BOLB); 

        this.stateMachine = new EntityStateMachine<>(this, new BolbWalkState());

        spawnOnRandomPlatform(); 
    }

    private void spawnOnRandomPlatform() {
        if (collisionObjects.size == 0) return;
        boolean valid = false;
        do {
            Rectangle platform = collisionObjects.random();

            float x = platform.x + platform.width / 2f - getWidth() / 2f;
            float y = platform.y + platform.height;
            setPosition(x , y);
            if (platform.width > 700f){
                valid = true;
            }
        } while (!valid);
    }

    public void update(float delta) {
        stateMachine.update(delta);
    }

    public void render(Batch batch) {
        stateMachine.render(batch);
    }

    public void receiveDamage(Entity source) {
        if (stateMachine.getCurrentState() instanceof BolbDamageState) return;
        Gdx.app.log("Atacante", source.toString());
        float centerTarget = this.getX() + this.getWidth() / 2f;
        float centerSource = source.getX() + source.getWidth() / 2f;
        float knockbackX = (centerTarget > centerSource) ? KNOCKBACK_FORCE_X : -KNOCKBACK_FORCE_X;

        this.velocity.x = knockbackX;
        this.velocity.y = KNOCKBACK_FORCE_Y;

        this.getStateMachine().changeState(new BolbDamageState(source));
    }


    public AnimationHandler getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(EnemyAnimationType type) {
        if (type == null) return;
        if (animations == null) return;
        if (type.ordinal() >= animations.length) return;

        AnimationHandler newAnimation = animations[type.ordinal()];
        if (newAnimation != null && newAnimation != currentAnimation) {
            currentAnimationType = type;
            currentAnimation = newAnimation;
            currentAnimation.reset();
        }
    }

    public EnemyAnimationType getCurrentAnimationType() {
        return currentAnimationType;
    }

    public float getSpeed() {
        return speed;
    }

    
    public void setAnimation(EnemyAnimationType type) {
        this.currentAnimation = animations[type.ordinal()];
    }


    public float getGravity() {
        return gravity;
    }

    public float getKnockbackTimer() {
        return knockbackTimer;
    }

    public void setKnockbackTimer(float knockbackTimer) {
        this.knockbackTimer = knockbackTimer;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Rectangle getBounds() {
        return bounds;
    }


    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public boolean isOnSolidGround() {
        for (Rectangle platform : collisionObjects) {
            if (collisionBox.overlaps(platform)) {
                return true;
            }
        }
        return false;
    }

    public float getCollisionOffsetX() {
        return 0;
    }

    public float getCollisionOffsetY() {
        return 0;
    }

    public boolean canAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean value) {
        this.canAttack = value;
    }

    public void updateAttackCooldown(float delta) {
        if (!canAttack) {
            attackCooldownTimer += delta;
            if (attackCooldownTimer >= 5f) {
                canAttack = true;
                attackCooldownTimer = 0f;
            }
        }
    }

    public EntityStateMachine<Bolb> getStateMachine() {
        return stateMachine;
    }

    @Override 
    public String toString () {
        return "Bolb";
    }

}