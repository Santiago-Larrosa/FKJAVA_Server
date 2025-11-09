package com.FK.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.FK.game.core.MainGame;
import com.FK.game.network.ServerThread;
import com.FK.game.animations.UIAssets;

public class ServerLauncherScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;
    private ServerThread server;
    private boolean serverStarted = false;

    public ServerLauncherScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = UIAssets.glassySkin;

        Label title = new Label("Servidor del Juego", skin, "default");
        title.setAlignment(Align.center);
        title.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() - 100);

        TextButton startServerButton = new TextButton("Iniciar Servidor", skin);
        startServerButton.setSize(250, 60);
        startServerButton.setPosition(Gdx.graphics.getWidth() / 2f - 125, Gdx.graphics.getHeight() / 2f - 30);

        startServerButton.addListener(event -> {
            if (startServerButton.isPressed() && !serverStarted) {
                startServer();
                serverStarted = true;
                return true;
            }
            return false;
        });

        stage.addActor(title);
        stage.addActor(startServerButton);
    }

    private void startServer() {
        server = new ServerThread();
        server.start();
        System.out.println("[SERVER] Servidor iniciado desde pantalla inicial.");
        game.server = server;
        game.setScreen(new LoadingScreen(game)); // true = modo servidor
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { stage.dispose(); }
    @Override public void dispose() { stage.dispose(); if (skin != null) skin.dispose(); }
}
