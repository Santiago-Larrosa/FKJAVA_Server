// Crea un nuevo archivo: Fungo.java
package com.FK.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.FK.game.animations.EnemyAnimationType;
import com.FK.game.entities.*;
import com.FK.game.states.*;
import com.FK.game.animations.*;
import com.FK.game.core.GameContext;
import com.FK.game.network.*;

public class Fungop extends Enemy<Fungop> {
   
    public Fungop(Array<Rectangle> collisionObjects) {
        super(0, 0, 200, 269, 120, 150, collisionObjects);
        setCollisionBoxOffset(35f, 35f); 
        this.networkId = GameContext.getNextEntityId();
        setDamage(2);
        setKnockbackX(200f);
        this.attackRange = 200f;
        this.entityTypeMessage = EntityTypeMessage.FUNGOP;
        setKnockbackY(400f);
        this.maxHealth = 10; 
        this.health = this.maxHealth; 
        this.attackCooldownTimer = 5f; 
        this.setCanAttack(true);
        this.coinValue = 10;
        setCurrentAnimation(EnemyAnimationType.FUNGOP);
        initStateMachine();
        setGravity(-250f);
    }
    
    
    @Override
    protected EnemyDamageState<Fungop> createDamageState(Entity source) {
        return new EnemyDamageState<>(source);
    }

    @Override
    public void updatePlayerDetection() {
        this.isPlayerInRange = false;

        final float ATTACK_DETECTION_WIDTH = 150f;
        final float ATTACK_DETECTION_HEIGHT = 400f;

        Rectangle detectionBox = new Rectangle(
            this.getCollisionBox().x - (ATTACK_DETECTION_WIDTH / 2f) + (this.getCollisionBox().width / 2f),
            this.getCollisionBox().y - ATTACK_DETECTION_HEIGHT,
            ATTACK_DETECTION_WIDTH,
            ATTACK_DETECTION_HEIGHT
        );

        for (Player player : GameContext.getActivePlayers()) {
            if (player != null && !player.isDead()) {
                if (detectionBox.overlaps(player.getCollisionBox())) {
                    this.isPlayerInRange = true;
                }
            }
        }
    }

    @Override
public AnimationType getDamageAnimationType() {
    return isMovingRight() ? EnemyAnimationType.FUNGOP : EnemyAnimationType.FUNGOP;
}

    @Override
    public EntityState<Fungop> createDefaultState() {
        return new FungoFlyingState();
    }

    @Override
    public String toString() {
        return "Fungop";
    }
}
