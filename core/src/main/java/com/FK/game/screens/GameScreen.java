package com.FK.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;  
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.FK.game.sounds.*;
import com.FK.game.maps.*;

public class GameScreen implements Screen {
    private final MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Player player;
    private FireAttackHUD fireAttackHUD;
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
    private float shakeDuration = 0f;
    private float shakeIntensity = 0f;
    private float shakeTime = 0f;
    private float originalCamX, originalCamY;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<Rectangle> collisionObjects = new Array<Rectangle>();
    private ShapeRenderer shapeRenderer;
    private Array<Enemy> enemies; 
    private Array<Entity> entities;
    private Rectangle playerSpawnPoint; 
    private Portal portal;
    

    public GameScreen(MainGame game) {
        this.game = game;
    }

  @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        entities = new Array<>();
        enemies = new Array<>();
        
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH * 0.7f, WORLD_HEIGHT * 0.7f, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH/2 * 0.7f, WORLD_HEIGHT/2 * 0.7f, 0);
  
        if (!AnimationCache.getInstance().update()) {
            game.setScreen(new LoadingScreen(game));
            return;
        }
        loadNewRandomMap();
    }

private void checkPortalCollision() {
    if (portal != null && player != null) {
        if (player.getCollisionBox().overlaps(portal.getCollisionBox())) {
            Gdx.app.log("PORTAL", "Jugador entró al portal, cargando nuevo mapa...");
            loadNewRandomMap();
        }
    }
}


    private void loadNewRandomMap() {
        FireAttackHUD existingHUD = player != null ? player.getFireAttackHUD() : null;
        
        cleanUpCurrentMap();
        
        MapManager mapManager = new MapManager(0.7f);
        mapManager.loadMaps(
            "maps/room3.tmx",

            "maps/room6.tmx"
        );
        mapManager.setRandomMap();
        map = mapManager.getCurrentMap();
        mapRenderer = new OrthogonalTiledMapRenderer(map, mapManager.getScale());
        loadCollisionObjects(mapManager.getScale());

        if (!AnimationCache.getInstance().update()) {
            Gdx.app.log("ANIMATION", "Recargando animaciones...");
            game.setScreen(new LoadingScreen(game));
            return;
        }

        loadEntities(mapManager.getScale(), existingHUD);
        
    }

    private void cleanUpCurrentMap() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        collisionObjects.clear();
        entities.clear();
        enemies.clear();
        portal = null;
    }

    private void loadEntities(float scale, FireAttackHUD existingHUD) {
        Array<Rectangle> playerSpawns = loadSpawnPoints("Player", scale);
        Array<Rectangle> bolbSpawns = loadSpawnPoints("Bolb", scale);
        Array<Rectangle> slopSpawns = loadSpawnPoints("Slop", scale);

        if (playerSpawns.size > 0) {
            Rectangle spawn = playerSpawns.first();
            playerSpawnPoint = spawn;
            
            player = new Player(game);
            player.setCurrentAnimation(PlayerAnimationType.IDLE_RIGHT);
            player.setPosition(spawn.x, spawn.y);
            player.setCollisionObjects(collisionObjects);
            
            if (existingHUD != null) {
                player.setFireAttackHUD(existingHUD);
            } else {
                fireAttackHUD = new FireAttackHUD();
                player.setFireAttackHUD(fireAttackHUD);
            }
            
            entities.add(player);
            GameContext.setPlayer(player);
        }

        for (Rectangle spawn : bolbSpawns) {
            Enemy bolb = new Bolb(collisionObjects);
            bolb.setPosition(spawn.x, spawn.y);
            enemies.add(bolb);
            entities.add(bolb);
        }

        for (Rectangle spawn : slopSpawns) {
            Enemy slop = new Slop(collisionObjects);
            slop.setPosition(spawn.x, spawn.y);
            enemies.add(slop);
            entities.add(slop);
        }
    }
    private void loadCollisionObjects(float scale) {
        MapLayer collisionLayer = map.getLayers().get("Capa de Objetos 1");
        
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    rect.x *= scale;
                    rect.y *= scale;
                    rect.width *= scale;
                    rect.height *= scale;
                    collisionObjects.add(rect);
                }
            }
        } else {
            Gdx.app.log("DEBUG", "No se encontró la capa de colisiones");
        }
    }

    private void updateEntities(float delta) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            
            if (e instanceof Enemy && ((Enemy) e).isDead()) { 
                entities.removeIndex(i);
                continue;
            }
            
            float oldX = e.getX();
            float oldY = e.getY();
            
            e.update(delta);

            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;
                enemy.updateAttackCooldown(delta);
            }

            Rectangle bounds = e.getCollisionBox();
            boolean collisionX = false;
            boolean collisionY = false;

            for (Rectangle rect : collisionObjects) {
                if (bounds.overlaps(rect)) {
                    float overlapX = Math.min(
                        bounds.x + bounds.width - rect.x,
                        rect.x + rect.width - bounds.x
                    );

                    float overlapY = Math.min(
                        bounds.y + bounds.height - rect.y,
                        rect.y + rect.height - bounds.y
                    );

                    if (overlapX < overlapY) collisionX = true;
                    else collisionY = true;
                }
            }

        if (collisionX) {
            e.setPosition(oldX, e.getY());
            e.setHasWallAhead(true);

            String entityType = e.getClass().toString(); 
            Gdx.app.log(entityType, "Colisión horizontal: pared detectada");
        } else {
            e.setHasWallAhead(false);
        }

            
            if (collisionY) e.setPosition(e.getX(), oldY);
        }
        
        checkEntityDamage();
        fireAttackHUD.update(delta);
        if (entities.size == 1 && entities.first() instanceof Player && portal == null) {
            portal = new Portal(playerSpawnPoint.x, playerSpawnPoint.y);
            entities.add(portal);
            Gdx.app.log("PORTAL", "Portal creado en (" + playerSpawnPoint.x + ", " + playerSpawnPoint.y + ")");
        }
        checkPortalCollision();

    }



    private void checkEntityDamage() {
        for (int i = 0; i < entities.size; i++) {
            Entity attacker = entities.get(i);
            Rectangle damageBox = attacker.getDamageBox();

            if (damageBox.width == 0 || damageBox.height == 0) continue;

            for (int j = 0; j < entities.size; j++) {
                if (i == j) continue;

                Entity target = entities.get(j);

                if (damageBox.overlaps(target.getCollisionBox())) {
                    
                    target.receiveDamage(attacker);
                }
            }
        }
    }

    private Array<Rectangle> loadSpawnPoints(String layerName, float scale) {
    Array<Rectangle> spawnPoints = new Array<>();
    MapLayer layer = map.getLayers().get(layerName);

    if (layer != null) {
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                rect.x *= scale;
                rect.y *= scale;
                rect.width *= scale;
                rect.height *= scale;
                spawnPoints.add(rect);
            }
        }
    } else {
        Gdx.app.log("DEBUG", "No se encontró la capa: " + layerName);
    }
    return spawnPoints;
}



  @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        updateEntities(delta);
        updateCamera(delta);
        
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            for (Entity e : entities) {
                e.render(batch);
            }
            fireAttackHUD.render(batch, camera);
        }
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        for (Entity e: this.entities) {
            e.renderDebug(shapeRenderer);
            e.renderDebugDamage(shapeRenderer);
        }
        
    }

    public FireAttackHUD getFireAttackHUD() {
        return fireAttackHUD;
    }

    

        
    private void updateCamera(float delta) {
        float playerCenterX = player.getBounds().x + player.getBounds().width / 2f;
        float playerCenterY = player.getBounds().y + player.getBounds().height / 2f;
        float offsetX = player.isMovingRight() ? 100f : -100f; 
        float offsetY = 20f; 
        float targetX = playerCenterX + offsetX;
        float targetY = playerCenterY + offsetY;
        float lerpSpeed = 3f; 
        camera.position.x += (targetX - camera.position.x) * lerpSpeed * delta;
        camera.position.y += (targetY - camera.position.y) * lerpSpeed * delta;
        if (shakeTime < shakeDuration) {
            shakeTime += delta;
            float currentIntensity = shakeIntensity * (1 - (shakeTime / shakeDuration));
            float shakeX = MathUtils.random(-1f, 1f) * currentIntensity;
            float shakeY = MathUtils.random(-1f, 1f) * currentIntensity;
            camera.position.x += shakeX;
            camera.position.y += shakeY;
        } else if (shakeDuration > 0f) {
            shakeDuration = 0f;
        }

        camera.update();
    }

    public void shakeCamera(float duration, float intensity) {
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
        this.shakeTime = 0f;
        this.originalCamX = camera.position.x;
        this.originalCamY = camera.position.y;
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
        for (Entity e : entities) {
            e.dispose();
        }   
        map.dispose();
        mapRenderer.dispose();
        collisionObjects.clear();
    }
}