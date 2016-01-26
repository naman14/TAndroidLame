package com.naman14.androidlame;

/**
 * Created by naman on 26/01/16.
 */
public class AndroidLame {


    static {
        System.loadLibrary("androidlame");
    }

    public AndroidLame() {
        initializeDefault();
    }

    public AndroidLame(LameBuilder builder) {
        initialize(builder);
    }


    private void initialize(LameBuilder builder) {
        initialize(builder.inSampleRate, builder.outChannel, builder.outSampleRate,
                builder.outBitrate, builder.quality, builder.id3tagTitle, builder.id3tagArtist, builder.id3tagAlbum, builder.id3tagYear, builder.id3tagComment);
    }

    private static native void initializeDefault();

    private static native void initialize(int inSamplerate, int outChannel,
                                          int outSamplerate, int outBitrate, int quality, String id3tagTitle,
                                          String id3tagArtist, String id3tagAlbum, String id3tagYear,
                                          String id3tagComment);

    private native static int encode(short[] buffer_l, short[] buffer_r,
                                     int samples, byte[] mp3buf);


    private native static int encodeBufferInterleaved(short[] pcm, int samples,
                                                      byte[] mp3buf);


    private native static int flush(byte[] mp3buf);


    private native static void close();
}
