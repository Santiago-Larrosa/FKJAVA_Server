// Crea un nuevo archivo: Fungo.java
package com.FK.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.FK.game.animations.EnemyAnimationType;
import com.FK.game.entities.*;
import com.FK.game.states.*; // Importamos el nuevo estado

public class Fungop extends Enemy {
    
    // El constructor define el tamaño y comportamiento inicial del Fungo
    public Fungop(Array<Rectangle> collisionObjects) {
        // Tamaño del sprite, tamaño de la colisión, lista de plataformas
        super(0, 0, 200, 269, 120, 150, collisionObjects);
        setCollisionBoxOffset(35f, 35f); // Centramos la caja de colisión
        setDamage(2);
        this.attackCooldownTimer = 5f; 
        this.setCanAttack(true);
        
        
        // Su animación y estado iniciales
        setCurrentAnimation(EnemyAnimationType.FUNGOP);
        this.stateMachine = new EntityStateMachine<>(this, new FungoFlyingState());

    }
    
    
    @Override
    protected EnemyDamageState createDamageState(Entity source) {
        return new EnemyDamageState(source); // Puedes crear un DamageState específico si quieres
    }

    @Override
    public EntityState<Enemy> getDefaultState() {
        // Su estado por defecto siempre será volar
        return new FungoFlyingState();
    }

    @Override
    public String toString() {
        return "Fungop";
    }
}