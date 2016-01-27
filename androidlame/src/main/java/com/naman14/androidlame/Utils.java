package com.naman14.androidlame;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

/**
 * Created by naman on 27/01/16.
 */
public class Utils {

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
}
