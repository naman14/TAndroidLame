package com.naman14.androidlame;

/**
 * Created by naman on 26/01/16.
 */
public class LameBuilder {

    public int inSampleRate;
    public int outSampleRate;
    public int outBitrate;
    public int outChannel;
    public int quality;

    public String id3tagTitle;
    public String id3tagArtist;
    public String id3tagAlbum;
    public String id3tagComment;
    public String id3tagYear;


    public LameBuilder() {

        this.id3tagTitle = null;
        this.id3tagAlbum = null;
        this.id3tagArtist = null;
        this.id3tagComment = null;
        this.id3tagYear = null;

        this.inSampleRate = 8000;
        this.outSampleRate = 8000;
        this.outChannel = 1;
        this.outBitrate = 128;

        this.quality = 5;
    }

    public LameBuilder setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public LameBuilder setInSampleRate(int inSampleRate) {
        this.inSampleRate = inSampleRate;
        return this;
    }

    public LameBuilder setOutSampleRate(int outSampleRate) {
        this.outSampleRate = outSampleRate;
        return this;
    }

    public LameBuilder setOutBitrate(int bitrate) {
        this.outBitrate = bitrate;
        return this;
    }

    public LameBuilder setOutChannels(int channels) {
        this.outChannel = channels;
        return this;
    }

    public LameBuilder setId3tagTitle(String title) {
        this.id3tagTitle = title;
        return this;
    }

    public LameBuilder setId3tagArtist(String artist) {
        this.id3tagArtist = artist;
        return this;
    }

    public LameBuilder setId3tagAlbum(String album) {
        this.id3tagAlbum = album;
        return this;
    }

    public LameBuilder setId3tagComment(String comment) {
        this.id3tagComment = comment;
        return this;
    }

    public LameBuilder setId3tagYear(String year) {
        this.id3tagYear = year;
        return this;
    }

    public AndroidLame build() {
        return new AndroidLame(this);
    }

}
