package com.naman14.androidlame;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by naman on 27/01/16.
 */
public class Utils {

    private static final int STREAM_BUFFER_SIZE = 4096;

    public static int getIntForMode(LameBuilder.Mode mode) {
        switch (mode) {
            case STEREO:
                return 0;
            case JSTEREO:
                return 1;
//            case DUAL_CHANNEL:
//                return 2;
            case MONO:
                return 3;
            case DEFAULT:
                return 4;
        }
        return -1;
    }


    public static int getSampleRate(String file) {
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);
        return mf.getInteger(MediaFormat.KEY_BIT_RATE);
    }

    public static int getChannelNumber(String file) {
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);
        return mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
    }

    public static int getBitRate(String file) {
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mf = mex.getTrackFormat(0);
        return mf.getInteger(MediaFormat.KEY_BIT_RATE);
    }

    public int read(File mInFile, short[] dst, int numSamples) throws IOException {
        FileInputStream fileStream = new FileInputStream(mInFile);
        BufferedInputStream mInStream = new BufferedInputStream(fileStream, STREAM_BUFFER_SIZE);

        byte[] buf = new byte[numSamples * 2];
        int index = 0;
        int bytesRead = mInStream.read(buf, 0, numSamples * 2);

        for (int i = 0; i < bytesRead; i += 2) {
            dst[index] = byteToShortLE(buf[i], buf[i + 1]);
            index++;
        }

        return index;
    }

    public int read(File mInFile, short[] left, short[] right, int numSamples) throws IOException {
        FileInputStream fileStream = new FileInputStream(mInFile);
        BufferedInputStream mInStream = new BufferedInputStream(fileStream, STREAM_BUFFER_SIZE);

        byte[] buf = new byte[numSamples * 4];
        int index = 0;
        int bytesRead = mInStream.read(buf, 0, numSamples * 4);

        for (int i = 0; i < bytesRead; i += 2) {
            short val = byteToShortLE(buf[0], buf[i + 1]);
            if (i % 4 == 0) {
                left[index] = val;
            } else {
                right[index] = val;
                index++;
            }
        }

        return index;
    }

    private static short byteToShortLE(byte b1, byte b2) {
        return (short) (b1 & 0xFF | ((b2 & 0xFF) << 8));
    }
}
