package com.FK.game.states;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
public class PlayerStateMachine {
    private Player player;
    private PlayerState currentState;
    private PlayerState previousState;

    public PlayerStateMachine(Player player) {
        this.player = player;
        this.currentState = new IdleState();
        this.currentState.enter(player);
    }

   public void changeState(PlayerState newState) {
        if (currentState != null) {
            currentState.exit(player);
        }
        this.currentState = newState;
        newState.enter(player);
    }

    public void update(float delta) {
        currentState.handleInput(player);
        currentState.update(player, delta); 
    }

    public void render(Batch batch) {
        currentState.render(player, batch);
    }

    public PlayerState getCurrentState() {
        return currentState;
    }

    public PlayerState getPreviousState() {
        return previousState;
    }

    public void revertToPreviousState() {
        if (previousState != null) {
            changeState(previousState);
        }
    }
}