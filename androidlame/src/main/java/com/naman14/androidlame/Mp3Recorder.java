package com.naman14.androidlame;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 简单整理录制逻辑到此类，有其他需求时视情况封装
 * Created by liyiheng on 16/12/29.
 */

public class Mp3Recorder {

    private int minBuffer;
    private int inSamplerate = 44100;
    private AudioRecord audioRecord;
    private AndroidLame androidLame;
    private FileOutputStream outputStream;
    private volatile boolean isRecording = false;
    private byte[] mp3buffer;
    public long length;
    private long mStartTime;


    public Mp3Recorder(File file) {
        minBuffer = AudioRecord.getMinBufferSize(inSamplerate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                inSamplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2);

        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        androidLame = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(1)
                .setOutBitrate(128)
                .setOutSampleRate(inSamplerate)
                .build();
    }


    public void pause() {
        isRecording = false;
        length += System.currentTimeMillis() - mStartTime;
    }


    public void start() {
        mStartTime = System.currentTimeMillis();
        audioRecord.startRecording();
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                short[] buffer = new short[inSamplerate * 2 * 5];
                mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];
                int bytesRead;
                while (isRecording) {
                    bytesRead = audioRecord.read(buffer, 0, minBuffer);
                    if (bytesRead > 0) {
                        int bytesEncoded = androidLame.encode(buffer, buffer, bytesRead, mp3buffer);
                        if (bytesEncoded > 0) {
                            try {
                                outputStream.write(mp3buffer, 0, bytesEncoded);
                            } catch (IOException e) {
                                e.printStackTrace();
                                pause();
                                if (listener != null) {
                                    listener.onError(e);
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void stop() {
        isRecording = false;
        int outputMp3buf = androidLame.flush(mp3buffer);
        if (outputMp3buf > 0) {
            try {
                outputStream.write(mp3buffer, 0, outputMp3buf);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        audioRecord.stop();
        audioRecord.release();
        androidLame.close();
        //length += System.currentTimeMillis() - mStartTime;
    }

    public interface OnErrorListener {
        /**
         * 可能在异步线程中调用
         */
        void onError(Throwable err);
    }

    private OnErrorListener listener;

    public void setOnErrorListener(OnErrorListener listener) {
        this.listener = listener;
    }
}
