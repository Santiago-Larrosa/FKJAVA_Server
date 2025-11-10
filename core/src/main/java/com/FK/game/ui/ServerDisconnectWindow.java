package com.FK.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.FK.game.core.MainGame;
import com.FK.game.core.GameContext;
import com.FK.game.network.ServerThread;

public class ServerDisconnectWindow extends Window {

    private final ServerThread server;
    private final MainGame game;

    public ServerDisconnectWindow(Skin skin, MainGame game, ServerThread server) {
        super("Finalizar Sesión", skin);
        this.server = server;
        this.game = game;

        setModal(true);
        setMovable(false);
        pad(30);
        getTitleLabel().setAlignment(Align.center);
        getTitleTable().padBottom(15);
        defaults().pad(10).growX();

        Label messageLabel = new Label(
            "¿Seguro que quieres finalizar la sesión?\nTodos los jugadores serán desconectados.",
            skin
        );
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);

        TextButton confirmButton = new TextButton("Sí, finalizar sesión", skin);
        TextButton cancelButton = new TextButton("Cancelar", skin);

        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmShutdown();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeWindow();
            }
        });

        add(messageLabel).padBottom(20).width(400);
        row();
        add(confirmButton).padTop(10).width(250);
        row();
        add(cancelButton).padTop(10).width(200).center();

        pack();
        getColor().a = 0f;
        addAction(Actions.fadeIn(0.25f));
    }

    private void confirmShutdown() {
        try {
            System.out.println("[SERVER_UI] Finalizando sesión desde ventana.");
            server.broadcastServerShutdown();
            server.clearServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeWindow();
    }

    private void closeWindow() {
        addAction(Actions.sequence(
            Actions.fadeOut(0.2f),
            Actions.run(this::remove)
        ));
    }

    public void centerWindow() {
        if (getStage() == null) return;
        float stageWidth = getStage().getWidth();
        float stageHeight = getStage().getHeight();
        float desiredWidth = Math.min(stageWidth * 0.5f, 500f);
        setSize(desiredWidth, getPrefHeight());
        setPosition(
            (stageWidth - getWidth()) / 2f,
            (stageHeight - getHeight()) / 2f
        );
    }
}
