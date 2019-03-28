package shu.eyespy;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashMap;

public class SoundManager {

    private static final float DEFAULT_MUSIC_VOLUME = 0.3F;
    public enum SoundEvent {
        PictureTaken,
        GameWon,
        GameLost
    }

    private static final int MAX_STREAMS = 1;

    private Context mContext;
    private SoundPool mSoundPool;
    private HashMap<SoundEvent, Integer> mSounds;
    private MediaPlayer mBackgroundMusicPlayer;

    SoundManager(Context context) {
        this.mContext = context;

        loadSounds();
        loadMusic();
    }

    private void loadSounds() {
        createSoundPool();

        mSounds = new HashMap<>();
        loadEventSound(mContext, SoundEvent.GameWon, "GameWon.mp3");
        loadEventSound(mContext, SoundEvent.GameLost, "GameLost.mp3");
        loadEventSound(mContext, SoundEvent.PictureTaken, "PictureTaken.mp3");
    }

    private void createSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(MAX_STREAMS)
                .build();
    }

    private void loadEventSound(Context context, SoundEvent event, String filename) {
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sfx/" + filename);
            int soundId = mSoundPool.load(descriptor, 1);
            mSounds.put(event, soundId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unloadSounds() {
        mSoundPool.release();
        mSoundPool = null;

        mSounds.clear();
    }

    public void playSoundForGameEvent(SoundEvent event) {
        Integer soundId = mSounds.get(event);
        if (soundId != null) {
            mSoundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    private void loadMusic() {
        try {
            mBackgroundMusicPlayer = new MediaPlayer();
            AssetFileDescriptor afd = mContext.getAssets()
                    .openFd("sfx/BackgroundLoop.mp3");
            mBackgroundMusicPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());
            mBackgroundMusicPlayer.setLooping(true);
            mBackgroundMusicPlayer.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
            mBackgroundMusicPlayer.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unloadMusic() {
        mBackgroundMusicPlayer.stop();
        mBackgroundMusicPlayer.release();
    }

    public void pauseBgMusic() {
        mBackgroundMusicPlayer.pause();
    }

    public void resumeBgMusic() {
        mBackgroundMusicPlayer.start();
    }
}
