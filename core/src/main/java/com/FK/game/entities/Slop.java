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
public class Slop extends Enemy {
    
    public Slop(Array<Rectangle> collisionObjects) {
        super(0, 0, 106, 75, 106, 75, collisionObjects);
        setCollisionBoxOffset(0f, 0f);
        setCurrentAnimation(EnemyAnimationType.SLOP);
        setDamage(1);
        this.stateMachine = new EntityStateMachine<>(this, new SlopWalkState());
        spawnOnRandomPlatform();
    }
    
    @Override
    protected EnemyDamageState createDamageState(Entity source) {
        return new EnemyDamageState(source);
    }
    @Override
    public EntityState<Enemy> getDefaultState() {
        return (EntityState<Enemy>) new SlopWalkState();
    }
    @Override
    public String toString() {
        return "Slop";
    }
}