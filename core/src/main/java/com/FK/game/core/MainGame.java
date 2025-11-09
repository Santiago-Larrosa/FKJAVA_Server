package com.FK.game.core;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.FK.game.network.ServerThread;
import com.FK.game.network.NetworkMessage;

public class MainGame extends Game {

    public PlayerData playerData;
    public PlayerData playerData2;
    public int roomsClearedCount = 0;
    public ServerThread server;
    private static MainGame instance;

public MainGame() {
    instance = this;
}
    @Override
    public void create() {
       // server = new ServerThread();
       // server.start();
        UIAssets.load();
        playerData = new PlayerData();
        playerData2 = new PlayerData();
        setScreen(new ServerLauncherScreen(this));
    }

    public void returnToServerLauncher() {
    if (server != null) {
        server.stopServer(); // o tu método para cerrar el hilo
        server = null;
    }
    setScreen(new ServerLauncherScreen(this));
    System.out.println("[MAIN] Volviendo a pantalla inicial del servidor.");
}
public static void onWindowClosed() {
    System.out.println("[MAIN] Cierre detectado desde ventana.");
    try {
        if (instance != null && instance.server != null) {
            instance.server.stopServer();
            System.out.println("[MAIN] Servidor detenido por cierre de ventana.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


}



//./gradlew build
//./gradlew :lwjgl3:run