package com.naman14.androidlame;

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
}
