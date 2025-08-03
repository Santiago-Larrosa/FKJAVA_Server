package com.FK.game.core;


import com.FK.game.entities.Player;


public class GameContext {
    private static Player player;

    public static void setPlayer(Player p) {
        player = p;
    }

    public static Player getPlayer() {
        return player;
    }
}
