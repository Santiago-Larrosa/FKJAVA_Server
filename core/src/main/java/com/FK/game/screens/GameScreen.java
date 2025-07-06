package com.FK.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;

public class GameScreen implements Screen {
    private final MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Player player;
    private boolean isCameraMoving = false;
    private float cameraMoveStartX, cameraMoveStartY;
    private float cameraMoveTargetX, cameraMoveTargetY;
    private float cameraMoveProgress = 0f;
    private final float CAMERA_TRANSITION_DURATION = 0.8f;
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;
    private float cameraOffsetX = 0;
    private float cameraOffsetY = 0;
    private final float CAMERA_MOVE_SPEED = 8f; 

    public GameScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        batch = new SpriteBatch();
        
        if (!AnimationCache.getInstance().update()) {
            game.setScreen(new LoadingScreen(game));
            return;
        }

        player = new Player();
        player.setPosition(WORLD_WIDTH/2 - player.getWidth()/2, WORLD_HEIGHT/2 - player.getHeight()/2);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        player.update(delta);
        updateCamera(delta);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }
    
    private void updateCamera(float delta) {
        float playerX = player.getX() + player.getWidth()/2;
        float playerY = player.getY() + player.getHeight()/2;
        
        if (!isCameraMoving) {
            float leftBound = camera.position.x - WORLD_WIDTH/2;
            float rightBound = camera.position.x + WORLD_WIDTH/2;
            float bottomBound = camera.position.y - WORLD_HEIGHT/2;
            float topBound = camera.position.y + WORLD_HEIGHT/2;
            
            if (playerX <= leftBound) {
                startCameraTransition(-WORLD_WIDTH, 0);
            } 
            else if (playerX >= rightBound) {
                startCameraTransition(WORLD_WIDTH, 0);
            }
            else if (playerY <= bottomBound) {
                startCameraTransition(0, -WORLD_HEIGHT);
            } 
            else if (playerY >= topBound) {
                startCameraTransition(0, WORLD_HEIGHT);
            }
        } else {
            updateCameraTransition(delta);
        }
        
        camera.update();
    }

    private void startCameraTransition(float offsetX, float offsetY) {
        isCameraMoving = true;
        cameraMoveProgress = 0f;
        cameraMoveStartX = camera.position.x;
        cameraMoveStartY = camera.position.y;
        cameraMoveTargetX = camera.position.x + offsetX;
        cameraMoveTargetY = camera.position.y + offsetY;
        player.setMovementLocked(true);
    }

    private void updateCameraTransition(float delta) {
        cameraMoveProgress += delta / CAMERA_TRANSITION_DURATION;
        float alpha = Math.min(1f, cameraMoveProgress);
        float smoothAlpha = MathUtils.lerp(0, 1, alpha); 
        
        camera.position.x = MathUtils.lerp(cameraMoveStartX, cameraMoveTargetX, smoothAlpha);
        camera.position.y = MathUtils.lerp(cameraMoveStartY, cameraMoveTargetY, smoothAlpha);
        
        if (cameraMoveProgress >= 1f) {
            isCameraMoving = false;
            cameraOffsetX = camera.position.x - WORLD_WIDTH/2;
            cameraOffsetY = camera.position.y - WORLD_HEIGHT/2;
            player.setMovementLocked(false); 
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
    }
}