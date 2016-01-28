package com.naman14.tandroidlame;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by naman on 28/01/16.
 */
public class Mp3AudioRecordActivity extends AppCompatActivity {

    int minBuffer;
    int inSamplerate = 8000;

    String filePath = Environment.getExternalStorageDirectory() + "/testrecord.mp3";

    boolean isRecording = true;

    AudioRecord audioRecord;
    AndroidLame androidLame;
    FileOutputStream outputStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        Button start = (Button) findViewById(R.id.startRecording);
        Button stop = (Button) findViewById(R.id.stopRecording);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
            }
        });

    }

    private void startRecording() {

        minBuffer = AudioRecord.getMinBufferSize(inSamplerate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, inSamplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2);

        short[] buffer = new short[inSamplerate * 2];

        // 'mp3buf' should be at least 7200 bytes long
        // to hold all possible emitted data.
        byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

        try {
            outputStream = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        androidLame = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(1)
                .build();

        audioRecord.startRecording();

        int bytesRead = 0;

        while (isRecording) {
            bytesRead = audioRecord.read(buffer, 0, minBuffer);

            if (bytesRead > 0) {

                int bytesEncoded = androidLame.encodeBufferInterLeaved(buffer, bytesRead, mp3buffer);

                if (bytesEncoded > 0) {
                    try {
                        outputStream.write(mp3buffer, 0, bytesEncoded);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        int outputMp3buf = androidLame.flush(mp3buffer);

        if (outputMp3buf > 0) {
            try {
                outputStream.write(mp3buffer, 0, outputMp3buf);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        audioRecord.stop();
        audioRecord.release();

        androidLame.close();

        isRecording = false;
    }
}
