package com.FK.game.states;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;

public interface PlayerState {
    void enter(Player player);
    void update(Player player, float delta);
    void handleInput(Player player);
    void render(Player player, Batch batch);
    void exit(Player player);
}