// Crea un nuevo archivo: UpgradeScreen.java
package com.FK.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.FK.game.core.MainGame;
import com.FK.game.screens.*;

public class UpgradeScreen extends AbstractUIScreen {

    private final GameScreen gameScreen;

    public UpgradeScreen(MainGame game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }

    @Override
    protected void buildStage() {
        // rootTable ya está creada y configurada en la clase padre.
        // Simplemente añadimos contenido.

        // --- Título ---
        Label titleLabel = new Label("MEJORAR PERSONAJE", skin); // usa "default"
 // "title" es un estilo definido en uiskin.json
        rootTable.add(titleLabel).padBottom(50);
        rootTable.row(); // Pasamos a la siguiente fila

        // --- Botón de Mejora de Daño ---
        // Aquí iría la lógica para comprobar si puedes comprar la mejora
        int currentDamage = 5; // Valor de ejemplo
        int upgradeCost = 100; // Valor de ejemplo
        
        TextButton damageButton = new TextButton("Mejorar Dano (" + currentDamage + ") - " + upgradeCost + " oro", skin);
        damageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("¡Botón de mejorar daño presionado!");
                // Aquí iría tu lógica para incrementar el daño del jugador
            }
        });
        rootTable.add(damageButton).width(400).pad(10);
        rootTable.row();

        // --- Botón de Mejora de Vida ---
        TextButton healthButton = new TextButton("Mejorar Vida", skin);
        rootTable.add(healthButton).width(400).pad(10);
        rootTable.row();
        
        // --- Botón para Volver al Juego ---
        TextButton backButton = new TextButton("Volver al Juego", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // CAMBIO: En lugar de crear una nueva GameScreen, volvemos a la que guardamos.
                game.setScreen(gameScreen);
            }
        });
        rootTable.add(backButton).width(200).padTop(50);

        // Opcional: Activa las líneas de debug para ver la estructura de la tabla
        rootTable.setDebug(true);

    }

    @Override
    public void hide() {
        // Es MUY IMPORTANTE devolver el control de input a tu juego cuando sales de una UI
        // Si no, no podrás mover a tu personaje.
        Gdx.input.setInputProcessor(null);
    }
}