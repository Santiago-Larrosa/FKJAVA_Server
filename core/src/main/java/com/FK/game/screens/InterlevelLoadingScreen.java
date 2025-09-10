
package com.FK.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.FK.game.core.MainGame;

public class InterlevelLoadingScreen implements Screen {
    private final MainGame game;
    private final GameScreen gameScreen; // Guardamos una referencia a la pantalla de juego
    private ShapeRenderer shapeRenderer;
    
    // Duración de la pantalla de carga en segundos
    private final float DURATION = 2.0f; 
    private float elapsedTime = 0;

    // El constructor ahora recibe la GameScreen para poder volver a ella
    public InterlevelLoadingScreen(MainGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }
   
    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        
        // Calculamos un progreso falso basado en el tiempo transcurrido
        float progress = Math.min(1f, elapsedTime / DURATION);

        // Dibujamos el fondo y la barra de progreso
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        // Usamos las mismas coordenadas que tu LoadingScreen original
        shapeRenderer.rect(Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2 - 15, 400 * progress, 30);
        shapeRenderer.end();
        
        // Cuando el tiempo ha pasado...
        if (elapsedTime >= DURATION) {
            gameScreen.loadRandomGameMap();
            game.setScreen(gameScreen);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
    
    // Métodos que no necesitamos pero deben estar por la interfaz Screen
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}