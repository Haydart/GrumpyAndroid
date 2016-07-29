package com.example.radek.grumpyandroid;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.annotation.RawRes;
import android.util.SparseIntArray;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Radek on 2016-07-29.
 */
public class SoundPlayer {
        private static final int[] RANT_SOUNDS = {
                R.raw.rant1,
                R.raw.rant2,
                R.raw.rant3,
                R.raw.rant4,
                R.raw.rant5,
                R.raw.rant6,
                R.raw.rant7,
                R.raw.rant8,
                R.raw.rant9,
                R.raw.rant10,
                R.raw.rant11,
                R.raw.rant12,
                R.raw.rant13,
                R.raw.rant14,
                R.raw.rant15
        };

        private SoundPool mSoundPool;
        private SparseIntArray mSoundsMap = new SparseIntArray(5);
        private Random mRandom;

        public SoundPlayer(final Context context, SoundPlayerInterface listener) {
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();

            mSoundsMap.put(R.raw.rant1, mSoundPool.load(context, R.raw.rant1, 1));
            mSoundsMap.put(R.raw.rant2, mSoundPool.load(context, R.raw.rant2, 1));
            mSoundsMap.put(R.raw.rant3, mSoundPool.load(context, R.raw.rant3, 1));
            mSoundsMap.put(R.raw.rant4, mSoundPool.load(context, R.raw.rant4, 1));
            mSoundsMap.put(R.raw.rant5, mSoundPool.load(context, R.raw.rant5, 1));
            mSoundsMap.put(R.raw.rant6, mSoundPool.load(context, R.raw.rant6, 1));
            mSoundsMap.put(R.raw.rant7, mSoundPool.load(context, R.raw.rant7, 1));
            mSoundsMap.put(R.raw.rant8, mSoundPool.load(context, R.raw.rant8, 1));
            mSoundsMap.put(R.raw.rant9, mSoundPool.load(context, R.raw.rant9, 1));
            mSoundsMap.put(R.raw.rant10, mSoundPool.load(context, R.raw.rant10, 1));
            mSoundsMap.put(R.raw.rant11, mSoundPool.load(context, R.raw.rant11, 1));
            mSoundsMap.put(R.raw.rant12, mSoundPool.load(context, R.raw.rant12, 1));
            mSoundsMap.put(R.raw.rant13, mSoundPool.load(context, R.raw.rant13, 1));
            mSoundsMap.put(R.raw.rant14, mSoundPool.load(context, R.raw.rant14, 1));
            mSoundsMap.put(R.raw.rant15, mSoundPool.load(context, R.raw.rant15, 1));

            mRandom = new Random();
            listener.OnSoundsLoaded();
        }

        public void playRandomRantSound() {
            final int rantSoundIndex = mRandom.nextInt(RANT_SOUNDS.length);
            play(RANT_SOUNDS[rantSoundIndex]);
        }

        private void play(@RawRes int soundResId) {
            mSoundPool.play(mSoundsMap.get(soundResId), 0.99f, 0.99f, 0, 0, 1);
        }

        public void release() {
            mSoundPool.release();
            mSoundPool = null;
        }

        public interface SoundPlayerInterface{
            void OnSoundsLoaded();
        }
}
