//
// Created by Naman on 26/01/16.
//

#include "AndroidLame.h"
#include <jni.h>

lame_global_flags *glf;

JNIEXPORT void JNICALL Java_com_naman14_androidlame_AndroidLame_initializeDefault(
        JNIEnv *env, jclass cls) {

    glf = initializeDefault(env);
}

JNIEXPORT void JNICALL Java_com_naman14_androidlame_AndroidLame_initialize(
        JNIEnv *env, jclass cls, jint inSamplerate, jint outChannel,
        jint outSamplerate, jint outBitrate, jfloat scaleInput, jint mode, jint quality,
        jstring id3tagTitle, jstring id3tagArtist, jstring id3tagAlbum,
        jstring id3tagYear, jstring id3tagComment) {

    glf = initialize(env, inSamplerate, outChannel, outSamplerate, outBitrate, scaleInput, mode,
                     quality, id3tagTitle, id3tagArtist, id3tagAlbum, id3tagYear,
                     id3tagComment);
}

JNIEXPORT jint JNICALL Java_com_naman14_androidlame_AndroidLame_encode(
        JNIEnv *env, jclass cls, jshortArray buffer_l,
        jshortArray buffer_r, jint samples, jbyteArray mp3buf) {
    return encode(env, glf, buffer_l, buffer_r, samples, mp3buf);
}

JNIEXPORT jint JNICALL Java_com_naman14_androidlame_AndroidLame_encodeBufferInterleaved(
        JNIEnv *env, jclass cls, jshortArray pcm,
        jint samples, jbyteArray mp3buf) {
    return encodeBufferInterleaved(env, glf, pcm, samples, mp3buf);
}

JNIEXPORT jint JNICALL Java_com_naman14_androidlame_AndroidLame_flush(
        JNIEnv *env, jclass cls, jbyteArray mp3buf) {
    return flush(env, glf, mp3buf);
}

JNIEXPORT void JNICALL Java_com_naman14_androidlame_AndroidLame_close(
        JNIEnv *env, jclass cls) {
    close(glf);
}


lame_global_flags *initializeDefault(JNIEnv *env) {
    lame_global_flags *glf = lame_init();
    lame_init_params(glf);
    return glf;
}

lame_global_flags *initialize(
        JNIEnv *env,
        jint inSamplerate, jint outChannel,
        jint outSamplerate, jint outBitrate, jfloat scaleInput, jint mode, jint quality,
        jstring id3tagTitle, jstring id3tagArtist, jstring id3tagAlbum,
        jstring id3tagYear, jstring id3tagComment) {

    lame_global_flags *glf = lame_init();
    lame_set_in_samplerate(glf, inSamplerate);
    lame_set_num_channels(glf, outChannel);
    lame_set_out_samplerate(glf, outSamplerate);
    lame_set_brate(glf, outBitrate);
    lame_set_quality(glf, quality);
    lame_set_scale(glf, scaleInput);

    switch (mode) {
        case 0:
            lame_set_mode(glf, STEREO);
        case 1:
            lame_set_mode(glf, JOINT_STEREO);
        case 3:
            lame_set_mode(glf, MONO);
        case 4:
            lame_set_mode(glf, NOT_SET);
        default:
            lame_set_mode(glf, NOT_SET);
    }

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

jint encode(
        JNIEnv *env, lame_global_flags *glf,
        jshortArray buffer_l, jshortArray buffer_r,
        jint samples, jbyteArray mp3buf) {
    jshort *j_buffer_l = (*env)->GetShortArrayElements(env, buffer_l, NULL);

    jshort *j_buffer_r = (*env)->GetShortArrayElements(env, buffer_r, NULL);

    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_buffer(glf, j_buffer_l, j_buffer_r,
                                    samples, j_mp3buf, mp3buf_size);

    (*env)->ReleaseShortArrayElements(env, buffer_l, j_buffer_l, 0);
    (*env)->ReleaseShortArrayElements(env, buffer_r, j_buffer_r, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

jint encodeBufferInterleaved(
        JNIEnv *env, lame_global_flags *glf,
        jshortArray pcm, jint samples, jbyteArray mp3buf) {
    jshort *j_pcm = (*env)->GetShortArrayElements(env, pcm, NULL);

    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_buffer_interleaved(glf, j_pcm,
                                                samples, j_mp3buf, mp3buf_size);

    (*env)->ReleaseShortArrayElements(env, pcm, j_pcm, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

jint flush(
        JNIEnv *env, lame_global_flags *glf,
        jbyteArray mp3buf) {
    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_flush(glf, j_mp3buf, mp3buf_size);

    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

void close(
        lame_global_flags *glf) {
    lame_close(glf);
    glf = NULL;

}