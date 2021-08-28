package com.example.opengl_test3;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackManager {
    private Thread mRecordThread;
    AudioTrack mAudioTrack;
    private volatile static AudioTrackManager mInstance;

    public static AudioTrackManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioTrackManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioTrackManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * Play beep.
     * @param duration     the duration
     * @param frequency_hz the frequency hz
     */
    public void playBeep(int duration,int frequency_hz) {
        destroyThread();
        // int duration = 5; // duration of sound
        // double freqOfTone = 440; // hz

        final int sampleRate = 8000;
        final int numSamples = duration * sampleRate;
        final double samples[] = new double[numSamples];
        final short buffer[] = new short[numSamples];
        for (int i = 0; i < numSamples; ++i) {
            samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequency_hz));
            buffer[i] = (short) (samples[i] * Short.MAX_VALUE);
        }
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, buffer.length, AudioTrack.MODE_STATIC);

        Runnable recordRunnable = new Runnable() {
            @Override
            public void run() {
                mAudioTrack.write(buffer, 0, buffer.length);
                mAudioTrack.play();
            }
        };
        mRecordThread= new Thread(recordRunnable);
        mRecordThread.start();
    }
    private void destroyThread() {
        try {
            mAudioTrack.stop();
            mRecordThread.interrupt();
            mAudioTrack=null;
            mRecordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordThread = null;
            mAudioTrack=null;
        }
    }
}