package com.FK.game.entities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;


public class Player {
    private static final float WIDTH = 155;
    private static final float HEIGHT = 110;
    public static final float WALK_SPEED = 200f;
    public static final float GRAVITY = -600;
    public static final float JUMP_VELOCITY = 10450;
    public static final float FLOOR_Y = 100f; 
    private boolean facingRight = true;
    private boolean chargingJump = false;
    private final Texture texture = Assets.playerIdle;
    private final Texture pass = Assets.playerPass;
    private final Texture passLeft = Assets.playerPassLeft;
    private final Rectangle bounds;
    private final Vector2 velocity;
    private boolean onGround = true;
    private boolean isAttacking = false;
    private float attackTimeLeft = 0f;
    public static final float ATTACK_DURATION = 0.332f; 
    private PlayerState currentState;
    private AnimationHandler currentAnimation;
    private PlayerStateMachine stateMachine;
    private AnimationHandler[] animations;
    private PlayerAnimationType currentType;
    private boolean movementLocked = false;

    public Player() { 
        bounds = new Rectangle(100, FLOOR_Y, WIDTH, HEIGHT);
        velocity = new Vector2(0, 0);
        TextureLoader loader = new BasicTextureLoader(); 
        AnimationCache cache = AnimationCache.getInstance();
        this.animations = new AnimationHandler[PlayerAnimationType.values().length];
    
    for (PlayerAnimationType type : PlayerAnimationType.values()) {
        animations[type.ordinal()] = cache.getAnimation(type);
    }
        this.stateMachine = new PlayerStateMachine(this);
        currentState = new IdleState();
        currentState.enter(this); 
    }
   

    public void update(float delta) {
         if (!movementLocked) {
        stateMachine.update(delta); 
        applyPhysics(delta);         
        bounds.x += velocity.x * delta; 
    }
        
    }

    public void render(Batch batch) {
        currentState.render(this, batch);
    }

    public void setState(PlayerState newState) {
        currentState.exit(this);
        this.currentState = newState;
        newState.enter(this);
    }

     public PlayerStateMachine getStateMachine() {
        return stateMachine;
    }
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void applyPhysics(float delta) {
        if (!onGround) {
            velocity.x *= 0.98f;
        }
        
        velocity.y += GRAVITY * delta;
        bounds.y += velocity.y * delta;
        if (bounds.y <= FLOOR_Y) {
            bounds.y = FLOOR_Y;
            velocity.y = 0;
            onGround = true;
        }
    }

    public PlayerAnimationType getCurrentAnimationType() {
        return currentType;
    }

    public void setCurrentAnimation(PlayerAnimationType type) {
        if (type == null || type.ordinal() >= animations.length) {
            throw new IllegalArgumentException("Tipo de animación inválido");
        }
        this.currentAnimation = animations[type.ordinal()];
        if (currentAnimation == null) {
            throw new IllegalStateException("Animación no cargada para: " + type);
        }
    }

    public AnimationHandler getCurrentAnimation() {
        return currentAnimation;
    }

    public AnimationHandler getAnimation(PlayerAnimationType type) {
        return animations[type.ordinal()];
    }


    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Rectangle getBounds() { return bounds; }
    public Vector2 getVelocity() { return velocity; }

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

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    public void setMovementLocked(boolean locked) {
        this.movementLocked = locked;
    }

    public boolean isMovementLocked() {
        return movementLocked;
    }

   public void dispose() {
        for (AnimationHandler animation : animations) {
            if (animation != null) animation.dispose();
        }
        texture.dispose();
    }

}