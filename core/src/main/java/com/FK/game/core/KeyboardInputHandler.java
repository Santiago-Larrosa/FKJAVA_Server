package com.FK.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardInputHandler implements InputHandler {
    private final int keyLeft;
    private final int keyRight;
    private final int keyJump;
    private final int keyAttack;
    private final int keyFireAttack;
    private final int keyDown;

    public KeyboardInputHandler(int keyLeft, int keyRight, int keyJump, int keyAttack, int keyFireAttack, int keyDown) {
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyJump = keyJump;
        this.keyAttack = keyAttack;
        this.keyFireAttack = keyFireAttack;
        this.keyDown = keyDown;
    }

    @Override
    public boolean isMoveLeftPressed() {
        return Gdx.input.isKeyPressed(keyLeft);
    }

    @Override
    public boolean isMoveRightPressed() {
        return Gdx.input.isKeyPressed(keyRight);
    }

    @Override
    public boolean isJumpPressed() {
        return Gdx.input.isKeyPressed(keyJump); 
    }

    @Override
    public boolean isAttackJustPressed() {
        return Gdx.input.isKeyJustPressed(keyAttack);
    }

    @Override
    public boolean isFireAttackJustPressed() {
        return Gdx.input.isKeyJustPressed(keyFireAttack);
    }

    @Override
    public boolean isMoveDownJustPressed() {
        return Gdx.input.isKeyJustPressed(keyDown);
    }

    @Override
    public boolean isAttackPressed() {
        return Gdx.input.isKeyPressed(keyAttack);
    }
}