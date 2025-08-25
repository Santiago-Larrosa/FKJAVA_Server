package com.FK.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;  
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.FK.game.animations.AnimationHandler;
import com.FK.game.states.*;

public abstract class Entity<T extends Entity<T>> {
    protected Rectangle bounds;
    protected Vector2 velocity;
    protected boolean onGround = true;
    protected boolean onPlatform = false;
    protected boolean movementLocked = false;
    protected boolean movingRight = true;
    protected Rectangle collisionBox;
    protected float collisionOffsetX = 0;
    protected float collisionOffsetY = 0;
    protected static final float KNOCKBACK_FORCE_X = 400f;
    protected static final float KNOCKBACK_FORCE_Y = 300f;
    protected int health;
    protected float knockbackTimer = 0f;
    protected EntityStateMachine<T> stateMachine;
    protected AnimationHandler currentAnimation;
    protected AnimationHandler[] animations;
    protected Rectangle DamageBox;
    protected Array<Rectangle> collisionObjects;
    protected float damage = 0;
    private boolean hasWallAhead;

    public Entity(float x, float y, float width, float height, float CollisionBoxWidth, float colisionBoxHeight) {
        bounds = new Rectangle(x, y, width, height);
        velocity = new Vector2();
        collisionBox = new Rectangle(x, y, CollisionBoxWidth, colisionBoxHeight);
        this.DamageBox = new Rectangle(bounds.x, bounds.y, 0, 0);
    }

    public void update(float delta) {
        applyPhysics(delta);
        bounds.x += velocity.x * delta;
        bounds.y += velocity.y * delta;

        collisionBox.setPosition(bounds.x + collisionOffsetX, bounds.y + collisionOffsetY);
    }

    public void render(Batch batch) {
        if (currentAnimation != null) {
            TextureRegion frame = currentAnimation.getCurrentFrame();
            batch.draw(frame, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    

    protected void applyPhysics(float delta) {
        if (!onGround) {
            velocity.y += getGravity() * delta;
        } 
    }
    public boolean isMovingRight() {
        return movingRight;
    }

    public void receiveDamage(Entity source) {
        Gdx.app.log("DAÑO", this.getClass().getSimpleName() + " recibió daño de " + source.getClass().getSimpleName());
        float directionX = this.getX() + getWidth() / 2f - (source.getX() + source.getWidth() / 2f);
        float centerTarget = this.getX() + this.getWidth() / 2f;
        float centerSource = source.getX() + source.getWidth() / 2f;
        float knockbackX = (centerTarget > centerSource) ? KNOCKBACK_FORCE_X : -KNOCKBACK_FORCE_X;
        this.knockbackTimer = 0.3f;
        this.velocity.x = knockbackX;
        this.velocity.y = KNOCKBACK_FORCE_Y;

    }



    public void renderDebug(ShapeRenderer renderer) {
        renderer.setColor(Color.BLUE);
        renderer.begin(ShapeType.Line);
        renderer.rect(getCollisionBox().x, getCollisionBox().y, getCollisionBox().width, getCollisionBox().height);
        renderer.end();
    }

    public void renderDebugDamage(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.begin(ShapeType.Line);
        renderer.rect(DamageBox.x, DamageBox.y, DamageBox.width, DamageBox.height);
        renderer.end();
    }


    public EntityStateMachine<T> getStateMachine() {
        return stateMachine;
    }

    public abstract float getGravity();

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean value) {
        onGround = value;
    }

    public boolean isOnPlataform() {
        return onPlatform;
    }

    public void setOnPlatform(boolean value) {
        onPlatform = value;
    }

    public void setMovementLocked(boolean locked) {
        this.movementLocked = locked;
    }

    public boolean isMovementLocked() {
        return movementLocked;
    }

    public void setCollisionObjects(Array<Rectangle> objects) {
        this.collisionObjects = objects;
    }

    public void setDamage (float newDamage) {
        this.damage = newDamage;
    }

    public float getDamage () {
        return this.damage;
    }

    public Array<Rectangle> getCollisionObjects() {        
        return collisionObjects;
    }

    public Rectangle getCollisionBox() {
        return new Rectangle(getX() + collisionOffsetX, getY() + collisionOffsetY, collisionBox.width, collisionBox.height);
    }

    public boolean hasWallAhead() { return hasWallAhead; }
    public void setHasWallAhead(boolean value) { hasWallAhead = value; }

    public void setCollisionBoxOffset(float offsetX, float offsetY) {
        this.collisionOffsetX = offsetX;
        this.collisionOffsetY = offsetY;
    }

    public float getCollisionBoxOffsetX () {
        return this.collisionOffsetX;
    }

    public float getCollisionBoxOffsetY () {
        return this.collisionOffsetY;
    }

    public void setAnimation(AnimationHandler animation) {
        this.currentAnimation = animation;
    }

    public AnimationHandler getCurrentAnimation() {
        return currentAnimation;
    }

    public void setAnimations(AnimationHandler[] animations) {
        this.animations = animations;
    }

    public AnimationHandler getAnimation(int ordinal) {
        return animations[ordinal];
    }

    public void setCollisionBoxSize(float width, float height) {
        this.collisionBox.setSize(width, height);
    }

    public void setDamageSize(float width, float height) {
        DamageBox.setSize(width, height);
    }

    public void setBoundSize(float width, float height) {
        bounds.setSize(width, height);
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
        
    }

    public float getX() {
        return bounds.x;
    }
    public void decreaseHealth (float damage) {
        Gdx.app.log("Daño", "recibi daño");
        this.health -= damage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    public boolean isDead() {
        return health <= 0;
    }


    public float getY() {
        return bounds.y;
    }
    public float getCollisionWidth () {
        return this.collisionBox.width;
    }

    public float getCollisionHeight () {
        return this.collisionBox.height;
    }

    public boolean isOnSolidGround() {
        Rectangle sensor = new Rectangle(
            collisionBox.x,
            collisionBox.y - 2f,   
            collisionBox.width,
            4f
        );

        for (Rectangle platform : collisionObjects) {
            if (sensor.overlaps(platform)) {
                return true;
            }
        }

        return false;
    }

    public float getWidth() {
        return bounds.width;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public float getHeight() {
        return bounds.height;
    }

    public Rectangle getDamageBox() {
        return DamageBox;
    }

    public float getKnockbackX () {
        return this.KNOCKBACK_FORCE_X;
    }

    public float getKnockbackY () {
        return this.KNOCKBACK_FORCE_Y;
    }

    public void dispose() {
        for (AnimationHandler animation : animations) {
            if (animation != null) animation.dispose();
        }
    }
}
