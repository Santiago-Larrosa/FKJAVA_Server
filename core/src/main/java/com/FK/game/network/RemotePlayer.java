package com.FK.game.server;

import com.FK.game.states.EntityState;
import com.FK.game.network.FacingDirection;
import com.FK.game.network.StateMessage;

public class RemotePlayer {
    public float x, y;
    public boolean movingLeft, movingRight, jumping, attacking, fireAttacking, movingDown;
    public FacingDirection facing = FacingDirection.RIGHT;

    public void update(float delta) {
    }
}
