package com.FK.game.sounds;
import com.FK.game.animations.*;
import com.FK.game.core.*;
import com.FK.game.entities.*;
import com.FK.game.screens.*;
import com.FK.game.states.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public class SoundCache {
    private static SoundCache instance;
    private EnumMap<SoundType, Sound> soundMap;
    private Map<SoundType, Long> loopingSounds;

    private SoundCache() {
        soundMap = new EnumMap<>(SoundType.class);
        loopingSounds = new HashMap<>();
    }

    public static SoundCache getInstance() {
        if (instance == null) {
            instance = new SoundCache();
        }
        return instance;
    }

    public void loadAll() {
        for (SoundType type : SoundType.values()) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(type.getPath()));
            soundMap.put(type, sound);
        }
    }

    public Sound get(SoundType type) {
        return soundMap.get(type);
    }

    public void playLoop(SoundType type, float volume) {
        if (!loopingSounds.containsKey(type)) {
            long id = soundMap.get(type).loop(volume);
            loopingSounds.put(type, id);
        }
    }

    public void stopLoop(SoundType type) {
        Long id = loopingSounds.remove(type);
        if (id != null) {
            soundMap.get(type).stop(id);
        }
    }

    public void stopAllSounds() {
        for (Map.Entry<SoundType, Long> entry : loopingSounds.entrySet()) {
            Sound sound = soundMap.get(entry.getKey());
            sound.stop(entry.getValue());
        }
        loopingSounds.clear();

        for (Sound sound : soundMap.values()) {
            sound.stop();
        }
    }


    public void dispose() {
        for (Sound sound : soundMap.values()) {
            sound.dispose();
        }
        soundMap.clear();
        loopingSounds.clear();
    }
}