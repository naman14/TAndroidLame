package com.naman14.androidlame;

/**
 * Created by naman on 26/01/16.
 */
public class AndroidLame {


    static {
        System.loadLibrary("androidlame");
    }

    public AndroidLame() {
        initialize(new LameBuilder());
    }

    public AndroidLame(LameBuilder builder) {
        initialize(builder);
    }

    private void initialize(LameBuilder builder) {
        initialize(builder.inSampleRate, builder.outChannel, builder.outSampleRate,
                builder.outBitrate, builder.quality, builder.id3tagTitle, builder.id3tagArtist, builder.id3tagAlbum, builder.id3tagYear, builder.id3tagComment);
    }

    private static native void initialize(int inSamplerate, int outChannel,
                                          int outSamplerate, int outBitrate, int quality, String id3tagTitle,
                                          String id3tagArtist, String id3tagAlbum, String id3tagYear,
                                          String id3tagComment);
}
