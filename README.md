# TAndroidLame

AndroidLame is a wrapper Library for Android/Java around Lame MP3 encoder (http://lame.sourceforge.net/)   
Built using NDK and Andorid studio with experimental gradle plugin.

##Gradle dependency
####build.gradle (project)
```gradle
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
####app/build.gradle
```gradle
dependencies {
	        compile 'com.github.naman14:TAndroidLame:1.0'
	}
```	

#Usage

```java
AndoridLame androidLame = new AndroidLame(); //everything set to defaults
```
#####or

```java
LameBuilder builder = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(numChannels)
                .setOutBitrate(bitrate)
                .setOutSampleRate(outSamplerate)
                .setQuality(quality)
                .setVbrMode(vbrMode)
                .setVbrQuality(vbrQuality)
                .setScaleInput(scaleInput)
                .setId3tagTitle(title)
                .setId3tagAlbum(album)
                .setId3tagArtist(artist)
                .setId3tagYear(year)
                .setId3tagComment(comment)
                .setLowpassFreqency(freq)
                .setHighpassFreqency(freq)
                .setAbrMeanBitrate(meanBitRate);
              
AndroidLame androidLame = builder.build(); //use this
AndroidLame androidLame = new AndroidLame(builder); //or this
```
##Building with Android studio

Import the project and download the experimental gradle plugin and corresponding gradle version.  
`experimental-0.6.0alpha5` plugin version and `2.10` gradle version  
(http://tools.android.com/tech-docs/new-build-system/gradle-experimental)
Use `ndk-build` in androidlame/src/main/jni (where Android.mk is located) to generate the libandroidlame.so shared library  
(Android studio should automatically generate the so file when building but somehow it doesn't)

##Documentation

##LameBuilder

LameBuilder is a wrapper around the extra initialisation parameters in Lame.

**inSampleRate** - input sample rate in Hz.  default = 44100hz   
**numChannels** - number of channels in input stream. default=2  
**bitrate** - set the bitrate of out strteam
**outSampleRate** -   output sample rate in Hz.  default = 0, which means LAME picks best value  
  based on the amount of compression
**quality** - quality = 0 to 9.  0=best (very slow).  9=worst. default = 5  
**scaleInput** - scale the input by this amount before encoding.  default=1    

**vbrMode**
There are 3 bitrate modes in Lame - CBR, VBR, ABR  
**CBR** Constant Bit Rate (default) - CBR encodes every frame at the same bitrate.  
**VBR** Variable Bit Rate  - The final file size of a VBR encode is less predictable, but the quality is usually better.  
**ABR** Average Bit rate - A compromise between VBR and CBR modes, ABR encoding varies bits around a specified target bitrate. use `setAbrBitrate` to set the mean bitrate to be used for encoding

**setVbrMode**


