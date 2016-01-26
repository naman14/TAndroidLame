//
// Created by Naman on 26/01/16.
//

#include <jni.h>
#include "libmp3lame/lame.h"

#ifndef TANDROIDLAME_ANDROIDLAME_H
#define TANDROIDLAME_ANDROIDLAME_H

#ifdef __cplusplus
extern "C" {
#endif

lame_global_flags *initialize(
        JNIEnv *env,
        jint inSamplerate, jint outChannel,
        jint outSamplerate, jint outBitrate, jint quality,
        jstring id3tagTitle, jstring id3tagArtist, jstring id3tagAlbum,
        jstring id3tagYear, jstring id3tagComment);


#ifdef __cplusplus
}
#endif

#endif //TANDROIDLAME_ANDROIDLAME_H
