package com.FK.game.animations;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BasicTextureLoader implements TextureLoader {
    @Override
    public Texture loadTexture(String path) {
        return new Texture(path);
    }
}