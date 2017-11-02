# AndroidLame

AndroidLame is a wrapper Library for Android/Java around Lame MP3 encoder (http://lame.sourceforge.net/)   
Built using NDK and Andorid studio with experimental gradle plugin.

## Gradle dependency
#### build.gradle (project)
```gradle
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
#### app/build.gradle
```gradle
dependencies {
	        compile 'com.github.naman14:TAndroidLame:1.0'
	}
```	

# Usage

```java
AndoridLame androidLame = new AndroidLame(); //everything set to defaults
```
##### or

```java
LameBuilder builder = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(numChannels)
                .setOutBitrate(bitrate)
                .setOutSampleRate(outSamplerate)
                .setMode(mode)
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
## Building with Android studio

Import the project and download the experimental gradle plugin and corresponding gradle version.  
`experimental-0.6.0alpha5` plugin version and `2.10` gradle version  
(http://tools.android.com/tech-docs/new-build-system/gradle-experimental)
Use `ndk-build` in androidlame/src/main/jni (where Android.mk is located) to generate the libandroidlame.so shared library  
(Android studio should automatically generate the so file when building but somehow it doesn't)

## Documentation

## LameBuilder

LameBuilder is a wrapper around the extra initialisation parameters in Lame.

**inSampleRate** - input sample rate in Hz.  default = 44100hz     
**numChannels** - number of channels in input stream. default=2    
**bitrate** - set the bitrate of out stream  
**outSampleRate** -   output sample rate in Hz.  default = 0, which means LAME picks best value    
  based on the amount of compression   
**quality** - quality = 0 to 9.  0=best (very slow).  9=worst. default = 5    
**scaleInput** - scale the input by this amount before encoding.  default=1   

**Mode** - sets a preset mode  
```java 
public enum Mode {
        STEREO, JSTEREO, MONO, DEFAULT
    }
```

**vbrMode**
There are 3 bitrate modes in Lame - CBR, VBR, ABR  
  **CBR** Constant Bit Rate (default) - CBR encodes every frame at the same bitrate.  
  **VBR** Variable Bit Rate  - The final file size of a VBR encode is less predictable, but the quality is usually better.  
  **ABR** Average Bit rate - A compromise between VBR and CBR modes, ABR encoding varies bits around a specified target     bitrate. use `setAbrBitrate` to set the mean bitrate to be used for encoding

**setVbrMode**
default = VBR_OFF = CBR
```java
public enum VbrMode {
        VBR_OFF, VBR_RH, VBR_MTRH, VBR_ABR, VBR_DEFAUT
    }
```  
If using ABR, use `setAbrBitrate` to set the mean bitrate in kbps, value is ignored if used with other vbr modes  

**vbrQuality** VBR quality level.  0=highest  9=lowest, Range [0,...,10[     
**lowpassFrequency**  freq in Hz to apply lowpass. Default = 0 = lame chooses.  -1 = disabled  
**highpassFrequency** freq in Hz to apply highpass. Default = 0 = lame chooses.  -1 = disabled  

**setId3...** - to set id3 tags

## AndroidLame  
A wrapper class for actual native implementation and encoding    

`encode(short[] buffer_l, short[] buffer_r,  
                      int samples, byte[] mp3buf)`    
   input pcm data      
   returns number of bytes output in mp3buf    
     
`encodeBufferInterleaved(short[] pcm, int samples,  
                                                      byte[] mp3buf);`    
   as above, but input has L & R channel data interleaved.    
   num_samples = number of samples in the L (or R) channel, not the total number of samples in pcm[] 
      
`lameFlush(byte[] mp3buf);`  
  flushes the intenal PCM buffers, and returns the final mp3 frames, will also write id3v1 tags (if any) into the bitstream    returns number of bytes output to mp3buf    
  
  Demo
===============
  A sample apk is avilable in [releases](https://github.com/naman14/TAndroidLame/releases).  
  (Remember to grant permissions from settings on Marshmallow devices or app will crash)
  
  Sample apk has two demos -  
  - Encoding .wav to mp3 and   
  - Recording audio using AudioRecorder and encoding in real time to mp3    
    
<img src="https://raw.githubusercontent.com/naman14/TAndroidLame/master/app/Screenshot1.png" alt="alt text" width="250" height="500">
<img src="https://raw.githubusercontent.com/naman14/TAndroidLame/master/app/Screenshot2.png" alt="alt text" width="250" height="500">

## License

>(c) 2015 Naman Dwivedi 

>This is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. 

>This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 

>You should have received a copy of the GNU General Public License along with this app. If not, see <https://www.gnu.org/licenses/>.
