package com.FK.game.core; 

public interface InputHandler {
    boolean isMoveLeftPressed();
    boolean isMoveRightPressed();
    boolean isJumpPressed();
    boolean isAttackJustPressed();
    boolean isFireAttackJustPressed(); 
    boolean isMoveDownJustPressed(); 
    boolean isAttackPressed(); 
}