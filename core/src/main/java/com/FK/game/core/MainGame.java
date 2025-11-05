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
    @Override
    public void create() {
        server = new ServerThread();
        server.start();
        Assets.load(); 
        Assets.manager.finishLoading();
        Assets.assignTextures();
        playerData = new PlayerData();
        playerData2 = new PlayerData();
        setScreen(new LoadingScreen(this));
    }
}

//./gradlew build
//./gradlew :lwjgl3:run