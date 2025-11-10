package com.FK.game.core;


import com.FK.game.entities.Player;

import com.FK.game.screens.*;
import com.badlogic.gdx.utils.Array;


public class GameContext {
    private static Player player;

    private static GameScreen currentScreen;
    private static ServerLauncherScreen currentLauncherScreen;
    private static final Array<Player> activePlayers = new Array<>();

    public static Array<Player> getActivePlayers() {
        return activePlayers;
    }

    public static void addPlayer(Player player) {
        if (!activePlayers.contains(player, true)) {
            activePlayers.add(player);
        }
    }

    public static int getNextEntityId(){
        return currentScreen.requestEntityId();
    }
    public static void removePlayer(Player player) {
        activePlayers.removeValue(player, true);
    }
    
    public static void clearPlayers() {
        activePlayers.clear();
    }

    public static void setPlayer(Player p) {
        player = p;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void setScreen(GameScreen screen) {
        currentScreen = screen;
    }

    public static GameScreen getScreen() {
        return currentScreen;
    }

    public static void setLauncherScreen(ServerLauncherScreen screen) {
        currentLauncherScreen = screen;
    }

    public static ServerLauncherScreen getLauncherScreen() {
        return currentLauncherScreen;
    }
}
