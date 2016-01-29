# TAndroidLame

AndroidLame is a wrapper Library for Android/Java around Lame MP3 encoder.  
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
