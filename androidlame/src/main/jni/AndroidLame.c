//
// Created by Naman on 26/01/16.
//

#include "AndroidLame.h"
#include <jni.h>


JNIEXPORT void JNICALL Java_com_naman14_androidlame_AndroidLame_initialize(
        JNIEnv *env, jclass cls, jint inSamplerate, jint outChannel,
        jint outSamplerate, jint outBitrate, jint quality,
        jstring id3tagTitle, jstring id3tagArtist, jstring id3tagAlbum,
        jstring id3tagYear, jstring id3tagComment) {

    lame_global_flags *glf = initialize(env, inSamplerate, outChannel, outSamplerate, outBitrate,
                                        quality, id3tagTitle, id3tagArtist, id3tagAlbum, id3tagYear,
                                        id3tagComment);
}


lame_global_flags *initialize(
        JNIEnv *env,
        jint inSamplerate, jint outChannel,
        jint outSamplerate, jint outBitrate, jint quality,
        jstring id3tagTitle, jstring id3tagArtist, jstring id3tagAlbum,
        jstring id3tagYear, jstring id3tagComment) {

    lame_global_flags *glf = lame_init();
    lame_set_in_samplerate(glf, inSamplerate);
    lame_set_num_channels(glf, outChannel);
    lame_set_out_samplerate(glf, outSamplerate);
    lame_set_brate(glf, outBitrate);
    lame_set_quality(glf, quality);

    const jchar *title = NULL;
    const jchar *artist = NULL;
    const jchar *album = NULL;
    const jchar *year = NULL;
    const jchar *comment = NULL;
    if (id3tagTitle) {
        title = (*env)->GetStringChars(env, id3tagTitle, NULL);
    }
    if (id3tagArtist) {
        artist = (*env)->GetStringChars(env, id3tagArtist, NULL);
    }
    if (id3tagAlbum) {
        album = (*env)->GetStringChars(env, id3tagAlbum, NULL);
    }
    if (id3tagYear) {
        year = (*env)->GetStringChars(env, id3tagYear, NULL);
    }
    if (id3tagComment) {
        comment = (*env)->GetStringChars(env, id3tagComment, NULL);
    }

    if (title || artist || album || year || comment) {
        id3tag_init(glf);

        if (title) {
            id3tag_set_title(glf, (const char *) title);
            (*env)->ReleaseStringChars(env, id3tagTitle, title);
        }
        if (artist) {
            id3tag_set_artist(glf, (const char *) artist);
            (*env)->ReleaseStringChars(env, id3tagArtist, artist);
        }
        if (album) {
            id3tag_set_album(glf, (const char *) album);
            (*env)->ReleaseStringChars(env, id3tagAlbum, album);
        }
        if (year) {
            id3tag_set_year(glf, (const char *) year);
            (*env)->ReleaseStringChars(env, id3tagYear, year);
        }
        if (comment) {
            id3tag_set_comment(glf, (const char *) comment);
            (*env)->ReleaseStringChars(env, id3tagComment, comment);
        }
    }

    lame_init_params(glf);

    return glf;
}