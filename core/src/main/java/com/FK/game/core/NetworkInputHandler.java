package com.FK.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.FK.game.core.InputHandler;
import java.util.Queue;


public class NetworkInputHandler implements InputHandler {

    private volatile boolean moveLeft;
    private volatile boolean moveRight;
    private volatile boolean jump;
    private volatile boolean attack;
    private volatile boolean fire;
    private volatile boolean down;

     //   private final ConcurrentLinkedQueue<String> justPressedQueue = new ConcurrentLinkedQueue<>();


    public void handleNetworkInput(String input) {
        switch (input) {
            case "INPUT_LEFT" -> moveLeft = true;
            case "INPUT_RIGHT" -> moveRight = true;
            case "STOP_LEFT" -> moveLeft = false;
            case "STOP_RIGHT" -> moveRight = false;
            case "INPUT_JUMP" -> jump = true;
            case "STOP_JUMP" -> jump = false;
            case "INPUT_ATTACK" -> attack = true;
            case "STOP_ATTACK" -> attack = false;
            case "INPUT_FIRE" -> fire = true;
            case "INPUT_DOWN" -> down = true;
        }
    }

    @Override
    public boolean isMoveLeftPressed() {
        return moveLeft;
    }

    @Override
    public boolean isMoveRightPressed() {
        return moveRight;
    }

    @Override
public boolean isJumpPressed() {
    return jump;
}

@Override
public boolean isAttackJustPressed() {
    return attack;  
}
@Override
public boolean isAttackPressed() {
    return attack;
}
@Override
public boolean isFireAttackJustPressed() {
    return fire;
}

@Override
public boolean isMoveDownJustPressed() {
    return down;
}
/*
private boolean consumeAction(String action) {
    return justPressedQueue.remove(action);
}*/

}