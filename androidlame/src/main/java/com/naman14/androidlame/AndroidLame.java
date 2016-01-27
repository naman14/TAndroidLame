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
                builder.outBitrate, builder.scaleInput, Utils.getIntForMode(builder.mode), builder.quality, builder.id3tagTitle, builder.id3tagArtist,
                builder.id3tagAlbum, builder.id3tagYear, builder.id3tagComment);
    }

    public int encode(short[] buffer_l, short[] buffer_r,
                      int samples, byte[] mp3buf) {

        return lameEncode(buffer_l, buffer_r, samples, mp3buf);
    }

    public int encodeBufferInterLeaved(short[] pcm, int samples,
                                       byte[] mp3buf) {
        return encodeBufferInterleaved(pcm, samples, mp3buf);
    }

    public int flush(byte[] mp3buf) {
        return lameFlush(mp3buf);
    }

    public void close() {
        lameClose();
    }


    ///////////NATIVE
    private static native void initializeDefault();

    private static native void initialize(int inSamplerate, int outChannel,
                                          int outSamplerate, int outBitrate, float scaleInput, int mode,
                                          int quality, String id3tagTitle,
                                          String id3tagArtist, String id3tagAlbum, String id3tagYear,
                                          String id3tagComment);

    private native static int lameEncode(short[] buffer_l, short[] buffer_r,
                                         int samples, byte[] mp3buf);


    private native static int encodeBufferInterleaved(short[] pcm, int samples,
                                                      byte[] mp3buf);


    private native static int lameFlush(byte[] mp3buf);


    private native static void lameClose();
}
