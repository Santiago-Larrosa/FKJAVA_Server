package com.FK.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.FK.game.sounds.*;

public class StateUtils {
    private static final int REQUIRED_CONFIRMATIONS = 5;

    public static boolean checkFalling(Entity entity, float delta, int[] airConfirmationCount) {
        if (!entity.isOnPlataform()) {
            entity.getVelocity().y += entity.getGravity() * delta;
            airConfirmationCount[0]++;

            if (airConfirmationCount[0] >= REQUIRED_CONFIRMATIONS) {

                if (entity instanceof Player) {
                    entity.getStateMachine().changeState(new FallingState());
                }else {
                    entity.getStateMachine().changeState(new EnemyFallingState());
                }
                return true;
            }
        } else {
            entity.getVelocity().y = 0;
            airConfirmationCount[0] = 0;
        }
        return false;
    }
}
