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
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.scenes.scene2d.ui.Skin;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.utils.viewport.ScreenViewport;
    import com.FK.game.sounds.*;
    import com.FK.game.maps.*;
    import com.FK.game.ui.*;
    import com.badlogic.gdx.graphics.g2d.BitmapFont;
    import com.badlogic.gdx.graphics.g2d.ParticleEffect;
    import com.FK.game.network.*;
    import java.net.InetSocketAddress;

    


    public class GameScreen implements Screen {
        private final MainGame game;
        private OrthographicCamera camera;
        private Viewport viewport;
        private SpriteBatch batch;
        private Player player1;
        private Player player2;
        private Array<BaseHUD> hudElements;
        private BitmapFont hudFont;
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
        //private ShapeRenderer shapeRenderer;
        private Array<Enemy<?>> enemies; //-
        private Array<Entity> entities;
        private Rectangle playerSpawnPoint; 
        private final Color DarkViolet = new Color(0.1f, 0.05f, 0.15f, 1f); 
        private final Color LightViolet = new Color(0.25f, 0.15f, 0.3f, 1f);    
        private Rectangle portalSpawnPoint; 
        private Portal portal;
        private InputHandler player1Controls = new NetworkInputHandler();
        private Array<Player> players = new Array<>();
        private Array<Entity> syncedEntities = new Array<>();
        private InputHandler player2Controls = new NetworkInputHandler();
        private Texture whitePixelTexture;
        private TextureRegion whitePixelRegion; 
        private Array<ParticleEffect> activeEffects;
        private ParticleEffect groundImpactEffectTemplate;
        private boolean isFirstRun = true;
        private enum GameState {
            RUNNING,
            PAUSED
        }
        private GameState currentState = GameState.RUNNING;
        private Stage uiStage;
        private Skin uiSkin;
        private UpgradeWindow upgradeWindow;
        private ServerDisconnectWindow serverDisconnectWindow;
        private UpgradeManager upgradeManager;
        public GameScreen(MainGame game) {
            this.game = game;
        }
        

    @Override
        public void show() {
            //shapeRenderer = new ShapeRenderer();
            upgradeManager = new UpgradeManager();
            batch = new SpriteBatch();
            if (isFirstRun) {
                isFirstRun = false;
            entities = new Array<>();
            syncedEntities = new Array<>();
            enemies = new Array<>();
            activeEffects = new Array<>();
            GameContext.setScreen(this);
            camera = new OrthographicCamera();
            viewport = new StretchViewport(WORLD_WIDTH * 0.7f, WORLD_HEIGHT * 0.7f, camera);
            viewport.apply();
            camera.position.set(WORLD_WIDTH/2 * 0.7f, WORLD_HEIGHT/2 * 0.7f, 0);
            whitePixelTexture = Assets.whitePixel;  
            whitePixelRegion = new TextureRegion(whitePixelTexture);
            if (!AnimationCache.getInstance().update()) {
                game.setScreen(new LoadingScreen(game));
                return;
            }
            uiSkin = UIAssets.glassySkin;
            uiStage = new Stage(new ScreenViewport());
            hudFont = new BitmapFont();
            loadInitialMap(); 
            }
            Gdx.input.setInputProcessor(null);
        }

        private void loadInitialMap() {
            game.roomsClearedCount = 0;
            

            cleanUpCurrentMap();

            MapManager mapManager = new MapManager(0.7f);
            mapManager.loadMaps("maps/SpawnHall.tmx");
            map = mapManager.getMaps().first();
            mapRenderer = new OrthogonalTiledMapRenderer(map, mapManager.getScale());
            
            loadCollisionObjects(mapManager.getScale());
            
            Array<Rectangle> portalSpawns = loadSpawnPoints("Portal", mapManager.getScale());
            if (portalSpawns.size > 0) {
                this.portalSpawnPoint = portalSpawns.first();
            }
            loadEntities(mapManager.getScale(), null, true);
        }

        private void checkPortalCollision() {
    if (portal == null) return;

    for (Player player : GameContext.getActivePlayers()) {
        if (player == null || player.isDead()) continue;

        if (player.getCollisionBox().overlaps(portal.getCollisionBox())) {
            SoundCache.getInstance().stopAllSounds();
            game.roomsClearedCount++;

            for (Player p : GameContext.getActivePlayers()) {
                if (p != null && !p.isDead()) {
                    p.updatePlayerData();
                }
            }

            if (game.server != null) {
                game.server.sendPacketToAll("CHANGE_LEVEL");
            }

            game.setScreen(new InterlevelLoadingScreen(game, this));
        }
    }
}


        public MainGame getGame() {
            return game;
        }   
        public void loadSpecificMap(String mapName) {
            FireAttackHUD existingHUD = player1 != null ? player1.getFireAttackHUD() : null;
            
            cleanUpCurrentMap();

            MapManager mapManager = new MapManager(0.7f);
            mapManager.loadMaps(mapName);
            map = mapManager.getCurrentMap();
            mapRenderer = new OrthogonalTiledMapRenderer(map, mapManager.getScale());

            loadCollisionObjects(mapManager.getScale());

            Array<Rectangle> portalSpawns = loadSpawnPoints("Portal", mapManager.getScale());
            if (portalSpawns.size > 0) {
                this.portalSpawnPoint = portalSpawns.first();
            }

            loadEntities(mapManager.getScale(), existingHUD, false);
        }

        public String chooseNextMapName() {
            MapManager mapManager = new MapManager(0.7f);
            Array<String> commonRooms = new Array<>();
            commonRooms.add("maps/room3.tmx");
            commonRooms.add("maps/room4.tmx");
            commonRooms.add("maps/room5.tmx");
            commonRooms.add("maps/room6.tmx");
            String bossRoom = "maps/BossHall.tmx";

            if (game.roomsClearedCount >= 2) {
                Array<String> allPossibleRooms = new Array<>(commonRooms);
                allPossibleRooms.add(bossRoom);
                mapManager.loadMaps(allPossibleRooms.toArray(String.class));
            } else {
                mapManager.loadMaps(commonRooms.toArray(String.class));
            }

            mapManager.setRandomMap();
            TiledMap selectedMap = mapManager.getCurrentMap();
            String mapName = selectedMap.getProperties().get("fileName", String.class); 

            return mapName; 
        }


        private void cleanUpCurrentMap() {
            if (map != null) map.dispose();
            if (mapRenderer != null) mapRenderer.dispose();
            clearAllEntitiesAndNotify();
            collisionObjects.clear();
            entities.clear();
            syncedEntities.clear();
            enemies.clear();
            players.clear();
            portal = null;
            portalSpawnPoint = null;
        }
        public UpgradeManager getUpgradeManager() {
            return upgradeManager;
        }
        public void clearAllEntitiesAndNotify() {
            for (Entity e : syncedEntities) {
                int id = e.getNetworkId();
                game.server.sendPacketToAll("REMOVE_ENTITY:" + id);
            }
        }

    private void openUpgradeMenu(Player player) {
            if (player != null) {
            player.getStateMachine().changeState(new IdleState());
        }   
        System.out .println("Abriendo menú de mejoras...");
        player.setUpgradeMenuOpen(true);
            upgradeWindow = new UpgradeWindow(uiSkin, this::closeUpgradeMenu, player.getPlayerData(), upgradeManager);
            uiStage.addActor(upgradeWindow);
            upgradeWindow.centerWindow();
            Gdx.input.setInputProcessor(uiStage);
        }   
    private void openExitMenu() {
        System.out .println("Abriendo menú de salida");
            ServerDisconnectWindow
            serverDisconnectWindow = new ServerDisconnectWindow(uiSkin, game, game.server);
            uiStage.addActor(serverDisconnectWindow);
            serverDisconnectWindow.centerWindow();
            Gdx.input.setInputProcessor(uiStage);
        }
        public void closeUpgradeMenu() {
            System.out .println("Cerrando menú de mejoras...");
            currentState = GameState.RUNNING;
            upgradeWindow.remove(); 
            Gdx.input.setInputProcessor(null); 
            Gdx.app.log("GameScreen", "Menú de mejoras CERRADO. Juego reanudado.");
        }
        private void loadEntities(float scale, FireAttackHUD existingHUD, boolean isSpawnHall) {
            Array<Rectangle> playerSpawns = loadSpawnPoints("Player", scale);
            Array<Rectangle> bolbSpawns = loadSpawnPoints("Bolb", scale);
            Array<Rectangle> slopSpawns = loadSpawnPoints("Slop", scale);
            Array<Rectangle> fungopSpawns = loadSpawnPoints("Fungop", scale);
            Array<Rectangle> fireSpawns = loadSpawnPoints("Fire", scale);
            Array<Rectangle> bossSpawns = loadSpawnPoints("Boss", scale);

            if (playerSpawns.size > 0) {
                Rectangle spawn = playerSpawns.first();
                playerSpawnPoint = spawn;
                
                player1 = new Player(game, player1Controls, game.playerData);
                player1.setCurrentAnimation(PlayerAnimationType.IDLE_RIGHT);
                player1.setPosition(spawn.x, spawn.y);
                player1.setCollisionObjects(collisionObjects);
                hudElements = new Array<>();
                players.add(player1);
                
                entities.add(player1);
                GameContext.setPlayer(player1);

                player2 = new Player(game, player2Controls, game.playerData2);
                player2.setCurrentAnimation(PlayerAnimationType.IDLE_LEFT); 
                player2.setPosition(spawn.x, spawn.y); 
                player2.setCollisionObjects(collisionObjects);
                entities.add(player2);
                players.add(player2);
                FireAttackHUD fireHUD = new FireAttackHUD();
                FireAttackHUD fireHUD2 = new FireAttackHUD();
                player1.setFireAttackHUD(fireHUD); 
                player2.setFireAttackHUD(fireHUD2); 
                hudElements.add(fireHUD);

                CoinHUD coinHUD1 = new CoinHUD(game.playerData, Assets.coinTexture, hudFont);
                hudElements.add(coinHUD1);
                GameContext.addPlayer(player1); 
                GameContext.addPlayer(player2); 
            }

            for (Rectangle spawn : bolbSpawns) {
                Bolb bolb = new Bolb(collisionObjects);
                bolb.setPosition(spawn.x, spawn.y);
                enemies.add(bolb);
                entities.add(bolb);
                syncedEntities.add(bolb);
            }

            for (Rectangle spawn : slopSpawns) {
                Slop slop = new Slop(collisionObjects);
                slop.setPosition(spawn.x, spawn.y);
                enemies.add(slop);
                entities.add(slop);
                syncedEntities.add(slop);
            }

            for (Rectangle spawn : fungopSpawns) {
                Fungop fungop = new Fungop(collisionObjects);
                fungop.setPosition(spawn.x, spawn.y);
                enemies.add(fungop);
                entities.add(fungop);
                syncedEntities.add(fungop);
            }

            for (Rectangle spawn : bossSpawns) {
                Boss boss = new Boss(collisionObjects);
                boss.setPosition(spawn.x, spawn.y);
                enemies.add(boss);
                entities.add(boss);
                syncedEntities.add(boss);
            }

            for (Rectangle spawn : fireSpawns) {
                Fire fire = new Fire(spawn.x, spawn.y);
                entities.add(fire);
                syncedEntities.add(fire);
        
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
        
        public Array<Player> getPlayers() {
            return players;
        }
        public Player getPlayerById (int id) {
            if (id < 0 || id >= players.size) return null;
            return players.get(id);
        }
    private void updateEntities(float delta) {
        

        for (int i = entities.size - 1; i >= 0; i--) {
            
            Entity e = entities.get(i);

            if (e instanceof Player) {
                Player player = (Player) e;
                player.updateFireCooldown(delta); 
                
                int index = players.indexOf(player, true);
                /*if (player.isFireCharged()) {
                     game.server.sendPacketTo(index, "FIRE_READY");
                     System.out.println("[SERVER] Jugador " + index + " tiene fuego listo.");
                }else {
                    game.server.sendPacketTo(index, "FIRE_COOLDOWN");
                    System.out.println("[SERVER] Jugador " + index + " en cooldown de fuego.");
                }*/


                String stateName = player.getFireAttackHUD().getFireHudState();
                float stateTime = player.getFireAttackHUD().getFireHudStateTime();
                game.server.sendPacketTo(index, "HUD_FIRE_STATE:" + stateName + ":" + stateTime);

            }
            if (e instanceof CharacterEntity<?>) {
                ((CharacterEntity<?>) e).updateDamageCooldown(delta); 
            }
            if (e instanceof Enemy<?>) {
                Enemy<?> enemy = (Enemy<?>) e;
                enemy.updateAttackCooldown(delta); 
            }


            if (e instanceof Entity && e.isReadyForRemoval()) {

                int idToRemove = ((Entity) e).getNetworkId();

                if (e instanceof Player) {
                    handlePlayerDeath((Player) e);
                    return;
                }

                if (e instanceof Enemy<?>) {
                    Enemy<?> en = (Enemy<?>) e;
                    Rectangle spawnArea = en.getCollisionBox();
                    for (int j = 0; j < en.getCoinValue(); j++) {
                        float spawnX = MathUtils.random(spawnArea.x, spawnArea.x + spawnArea.width);
                        float spawnY = MathUtils.random(spawnArea.y, spawnArea.y + spawnArea.height);
                        Coin coin = new Coin(spawnX, spawnY);
                        entities.add(coin);
                        syncedEntities.add(coin);
                    }
                    enemies.removeValue(en, true);
                }

                entities.removeIndex(i);
                for (int j = 0; j < syncedEntities.size; j++) {
                    Entity ent = syncedEntities.get(j);
                    if (ent.getNetworkId() == idToRemove) {
                        syncedEntities.removeIndex(j);
                        break;
                    }
                }


                String removeMsg = "REMOVE_ENTITY:" + idToRemove;
                game.server.sendPacketToAll(removeMsg);

                continue; 
            }

            
            
            float oldX = e.getX();
            float oldY = e.getY();
            
            e.update(delta);


            Rectangle bounds = e.getCollisionBox();
            boolean collisionX = false;
            boolean collisionY = false;
            
            if (!(e instanceof Coin)){
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

                        if (overlapX < overlapY) {
                            collisionX = true;
                        } else {
                            collisionY = true;
                        }
                    }
                }
            }
            
            if (collisionX) {
                e.setPosition(oldX, e.getY());
                e.setHasWallAhead(true);
                e.setVelocityX(0);
            } else {
                e.setHasWallAhead(false);
            }

            boolean landedOnPlatform = false; 
            if (collisionY) {
                if (e.getVelocity().y <= 0) { 
                    landedOnPlatform = true;
                    e.getVelocity().y = 0; 
                } else { 
                    e.getVelocity().y = 0; 
                }
                e.setPosition(e.getX(), oldY);
            }
            
            e.setOnPlatform(landedOnPlatform); 


            if (e instanceof Coin) {
    Coin coin = (Coin) e;

    if (coin.getTarget() == null) {
        Vector2 coinCenter = coin.getCenter();

        float distP1 = (player1 != null && !player1.isDead())
            ? player1.getCenter().dst(coinCenter)
            : Float.MAX_VALUE;

        float distP2 = (player2 != null && !player2.isDead())
            ? player2.getCenter().dst(coinCenter)
            : Float.MAX_VALUE;

        if (distP1 <= distP2) {
            coin.setTarget(player1);
        } else {
            coin.setTarget(player2);
        }
    }

    if (coin.getTarget() != null && coin.getCollisionBox().overlaps(coin.getTarget().getCollisionBox())) {
        Player targetPlayer = coin.getTarget();
        targetPlayer.addCoins(1);
        e.setReadyForRemoval(true);
        int index = players.indexOf(targetPlayer, true);
            game.server.sendPacketTo(index, "COIN_PICKED:" + targetPlayer.getCoinCount());

    }
}


        }
        for (int i = 0; i < syncedEntities.size; i++) {
            Entity e = syncedEntities.get(i);
                    int id = e.getNetworkId(); 
                    String type = e.getTypeName(); 
                    float x = e.getX();
                    float y = e.getY();
                    float rotation = 0f;

                    String state = "NONE";
                    String facing = "NONE";

                    state = e.getStateMessage().toString();
                    facing = e.isMovingRight() ? "RIGHT" : "LEFT";

                    if (e instanceof Enemy<?>) {
                        Enemy<?> enemy = (Enemy<?>) e;
                        if (enemy.hasRotation()) {
                            rotation = enemy.getRotation();
                        }
                    }

                    

                    String msg = "UPDATE_ENTITY:" + id + ":" + type + ":" +
                                x + ":" + y + ":" + state + ":" + facing + ":" + rotation;
                   // System.out.println("Sending entity update: " + msg);
                   if (e instanceof Coin) {
                        System.out.println("Sending Coin update: " + msg);
                   }
                    game.server.sendPacketToAll(msg);
        }

        checkEntityDamage(); 
        if (enemies.isEmpty() && portal == null && portalSpawnPoint != null) {
            portal = new Portal(portalSpawnPoint.x, portalSpawnPoint.y);
            entities.add(portal);
            syncedEntities.add(portal);
            String msg = "CREATE_ENTITY:PORTAL:" + portalSpawnPoint.x + ":" + portalSpawnPoint.y;
        }
        checkPortalCollision();
    }
    
            public void sendFullEntitySnapshotTo(InetSocketAddress targetAddress) {
    for (Entity e : syncedEntities) {
        int id = e.getNetworkId(); 
                    String type = e.getTypeName(); 
                    float x = e.getX();
                    float y = e.getY();
                    float rotation = 0f;

                    String state = "NONE";
                    String facing = "NONE";

                    state = e.getStateMessage().toString();
                    facing = e.isMovingRight() ? "RIGHT" : "LEFT";

                    if (e instanceof Enemy<?>) {
                        Enemy<?> enemy = (Enemy<?>) e;
                        if (enemy.hasRotation()) {
                            rotation = enemy.getRotation();
                        }
                    }

      String msg = "CREATE_ENTITY:" + id + ":" + type + ":" +
               x + ":" + y + ":" + state + ":" + facing + ":" + rotation;
        game.server.sendPacket(msg, targetAddress);
    }
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
                        if (attacker instanceof Player && target instanceof Fire ) {
                            Player player = (Player) attacker;
                            int index = players.indexOf(player, true);
                            if (!player.isUpgradeMenuOpen()) {
                                if (!(attacker.getStateMachine().getCurrentState() instanceof FireAttackState)) {
                                player.getInputHandler().handleNetworkInput("STOP_ATTACK");
                                System.out.println("Jugador abrio el menú de mejoras cerca del fuego.");
                                game.server.sendPacketTo(index, "OPEN_UPGRADE_MENU");
                                openUpgradeMenu(player);
                                }
                            }
                            
                        } else {
                            if (target instanceof CharacterEntity) {
                                if (!(attacker instanceof Player && target instanceof Player)){
                                    ((CharacterEntity) target).receiveDamage(attacker);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void handlePlayerDeath(Player deadPlayer) {
            System.out.println("[SERVER] Jugador muerto: " + deadPlayer.getNetworkId());
            deadPlayer.getGame().playerData.resetOnDeath();
            if (deadPlayer.getGame().playerData2 != null) {
                deadPlayer.getGame().playerData2.resetOnDeath();
            }

            int index = players.indexOf(deadPlayer, true);
            if (game.server != null) {
                if (index >= 0) {
                    game.server.sendPacketToAll("PLAYER_DIED:" + index);
                }
                game.server.sendPacketToAll("CHANGE_LEVEL");
                game.server.sendPacketToAll("LEVEL_READY:maps/SpawnHall.tmx");
            }

            game.roomsClearedCount = 0;
            GameContext.clearPlayers();
            game.setScreen(new GameScreen(game));
        }

        public int requestEntityId() {
    return game.server.generateEntityId();
}
private Player getClosestAlivePlayer(Vector2 from) {
    Player best = null;
    float bestDist2 = Float.MAX_VALUE;

    for (Player p : players) {
        if (p != null && !p.isDead()) {
            float d2 = p.getCenter().dst2(from);
            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = p;
            }
        }
    }

    return best;
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
/*
    public void createImpactEffect(float x, float y) {
            if (groundImpactEffectTemplate == null) return;
            ParticleEffect effect = new ParticleEffect(groundImpactEffectTemplate);
            effect.setPosition(x, y);
            effect.start();
            activeEffects.add(effect);
        }
*/
    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            if (game.server != null) { 
                if (serverDisconnectWindow == null || !serverDisconnectWindow.hasParent()) {
                    openExitMenu();
                }
            }
        }

        updateEntities(delta);
        updateCamera(delta);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.07f, 1f); 
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiStage.act(Math.min(delta, 1 / 30f));
        uiStage.draw();
    }

        private void updateCamera(float delta) {
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
            uiStage.getViewport().update(width, height, true);
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
            for (Entity e : syncedEntities) {
                e.dispose();
            }
            map.dispose();
            mapRenderer.dispose();
            collisionObjects.clear();
           /* if (groundImpactEffectTemplate != null) {
                groundImpactEffectTemplate.dispose();
            }*/
            for (ParticleEffect effect : activeEffects) {
                effect.dispose();
            }
            hudFont.dispose();
            uiStage.dispose();
            whitePixelTexture.dispose();
            uiSkin.dispose();
        }
    }
