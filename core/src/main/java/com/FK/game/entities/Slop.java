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
import com.FK.game.network.*;

import java.util.Random;
public class Slop extends Enemy<Slop> {
    
    public Slop(Array<Rectangle> collisionObjects) {
        super(0, 0, 106, 75, 106, 75, collisionObjects);
        setCollisionBoxOffset(0f, 0f);
        this.networkId = GameContext.getNextEntityId();
        setCurrentAnimation(EnemyAnimationType.SLOP);
        this.maxHealth = 3; 
        this.attackRange = 50f;
        this.entityTypeMessage = EntityTypeMessage.SLOP;
        this.health = this.maxHealth; 
        setKnockbackX(100f);
        setKnockbackY(200f);
        setDamage(1);
        this.coinValue = 3;
        initStateMachine();
        spawnOnRandomPlatform();
    }
    
    @Override
    protected EnemyDamageState<Slop> createDamageState(Entity source) {
        return new EnemyDamageState<>(source);
    }
@Override
    public void updatePlayerDetection() {
        this.isPlayerInRange = false;
        for (Player player : GameContext.getActivePlayers()) {
            if (player != null && !player.isDead()) {
                if (this.getCenter().dst(player.getCenter()) < this.attackRange) {
                    this.isPlayerInRange = true;
                    return;
                }
            }
        }
    }
    @Override
public AnimationType getDamageAnimationType() {
    return isMovingRight() ? EnemyAnimationType.SLOP : EnemyAnimationType.SLOP_LEFT;
}

    @Override
    public EntityState<Slop> createDefaultState() {
        return new SlopWalkState();
    }
    @Override
    public String toString() {
        return "Slop";
    }
}
