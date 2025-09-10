package com.FK.game.core;


import com.FK.game.entities.Player;

import com.FK.game.screens.*;


public class GameContext {
    private static Player player;

    private static GameScreen currentScreen;

    public static void setPlayer(Player p) {
        player = p;
    }

    public static Player getPlayer() {
        return player;
    }

    // CAMBIO: Añadimos un setter para la pantalla
    public static void setScreen(GameScreen screen) {
        currentScreen = screen;
    }

    // CAMBIO: Añadimos un getter para la pantalla
    public static GameScreen getScreen() {
        return currentScreen;
    }
}
