// Crea un nuevo archivo: AbstractUIScreen.java
package com.FK.game.ui; // O donde guardes tus pantallas

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.FK.game.core.MainGame;

public abstract class AbstractUIScreen implements Screen {
    protected final MainGame game;
    protected Stage stage;
    protected Skin skin;
    protected Table rootTable; // La tabla principal que ocupará toda la pantalla

    public AbstractUIScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Cargamos el Skin que definirá el aspecto de nuestra UI
        skin = new Skin(Gdx.files.internal("ui/glassy-ui.json"));
        
        // Creamos el Stage con un viewport que se adapta al tamaño de la pantalla
        stage = new Stage(new ScreenViewport());

        // Creamos una tabla que será la raíz de nuestra UI y la hacemos llenar el escenario
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Llamamos al método que las clases hijas implementarán para construir su UI específica
        buildStage();
        
        // Hacemos que el Stage sea el que reciba los eventos de input (clics, etc.)
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Las clases que hereden de esta deben implementar este método
     * para añadir sus propios botones, etiquetas, etc., a la rootTable.
     */
    protected abstract void buildStage();

    @Override
    public void render(float delta) {
        // Limpiamos la pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizamos y dibujamos el escenario
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Liberamos los recursos
        stage.dispose();
        skin.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}