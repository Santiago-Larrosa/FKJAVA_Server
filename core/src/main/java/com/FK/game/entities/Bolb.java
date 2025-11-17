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
public class Bolb extends Enemy<Bolb> {
    
    public Bolb(Array<Rectangle> collisionObjects) {
        super(0, 0, 250, 300, 100, 150, collisionObjects);
        setCollisionBoxOffset(100f, 0f);
        this.networkId = GameContext.getNextEntityId();
        this.maxHealth = 5; 
        this.health = this.maxHealth; 
        this.entityTypeMessage = EntityTypeMessage.BOLB;
        setDamage(1);
        this.attackRange = 50f;
        setKnockbackX(100f);
        setKnockbackY(200f);
        this.coinValue = 5;
        setCurrentAnimation(EnemyAnimationType.BOLB);
        initStateMachine();
        spawnOnRandomPlatform();
    }

    @Override
    public AnimationType getDamageAnimationType() {
        return isMovingRight() ? EnemyAnimationType.BOLB : EnemyAnimationType.BOLB_LEFT;
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
    protected EnemyDamageState<Bolb> createDamageState(Entity source) {
        return new EnemyDamageState<>(source);
    }
    @Override
    public EntityState<Bolb> createDefaultState() {
        return new BolbWalkState();
    }
    @Override
    public String toString() {
        return "Bolb";
    }
}
