package com.FK.game.core;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AnimationCache {
    private static AnimationCache instance;
    private final AssetManager assetManager;
    private final ArrayMap<AnimationType, AnimationHandler> cache;

    private AnimationCache() {
        this.assetManager = new AssetManager();
        this.cache = new ArrayMap<>();
    }
    public static synchronized AnimationCache getInstance() {
        if (instance == null) {
            instance = new AnimationCache();
        }
        return instance;
    }

    public void loadAssets() {
        for (PlayerAnimationType type : PlayerAnimationType.values()) {
            assetManager.load(type.getTexturePath(), Texture.class);
        }

        for (EnemyAnimationType type : EnemyAnimationType.values()) {
            assetManager.load(type.getTexturePath(), Texture.class);
        }

    }

    public boolean update() {
        return assetManager.update();
    }

    public float getProgress() {
        return assetManager.getProgress();
    }

    public AnimationHandler getAnimation(AnimationType type) {
        if (!assetManager.isLoaded(type.getTexturePath())) {
            throw new GdxRuntimeException("Asset not loaded: " + type.getTexturePath());
        }
        
        if (!cache.containsKey(type)) {
            Texture texture = assetManager.get(type.getTexturePath(), Texture.class);
            cache.put(type, type.create(texture));
        }
        return cache.get(type);
    }

    public void dispose() {
        assetManager.dispose();
        cache.clear();
    }
}