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
import android.widget.TextView;
import android.widget.Toast;

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

    boolean isRecording = false;

    AudioRecord audioRecord;
    AndroidLame androidLame;
    FileOutputStream outputStream;

    LogFragment logFragment;
    TextView statusText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        Button start = (Button) findViewById(R.id.startRecording);
        Button stop = (Button) findViewById(R.id.stopRecording);

        statusText = (TextView) findViewById(R.id.statusText);
        logFragment = new LogFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.log_container, logFragment).commit();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    new Thread() {
                        @Override
                        public void run() {
                            isRecording = true;
                            startRecording();
                        }
                    }.start();

                } else
                    Toast.makeText(Mp3AudioRecordActivity.this, "Already recording", Toast.LENGTH_SHORT).show();
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

        addLog("Initialising audio recorder..");
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, inSamplerate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2);

        //5 seconds data
        addLog("creating short buffer array");
        short[] buffer = new short[inSamplerate * 2 * 5];

        // 'mp3buf' should be at least 7200 bytes long
        // to hold all possible emitted data.
        addLog("creating mp3 buffer");
        byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

        try {
            outputStream = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        addLog("Initialising Andorid Lame");
        androidLame = new LameBuilder()
                .setInSampleRate(inSamplerate)
                .setOutChannels(1)
                .build();

        addLog("started audio recording");
        updateStatus("Recording...");
        audioRecord.startRecording();

        int bytesRead = 0;

        while (isRecording) {

            addLog("reading to short array buffer, buffer sze- " + minBuffer);
            bytesRead = audioRecord.read(buffer, 0, minBuffer);
            addLog("bytes read=" + bytesRead);

            if (bytesRead > 0) {

                addLog("encoding bytes to mp3 buffer..");
                int bytesEncoded = androidLame.encodeBufferInterLeaved(buffer, bytesRead, mp3buffer);
                addLog("bytes encoded=" + bytesEncoded);

                if (bytesEncoded > 0) {
                    try {
                        addLog("writing mp3 buffer to outputstream with " + bytesEncoded + " bytes");
                        outputStream.write(mp3buffer, 0, bytesEncoded);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        addLog("stopped recording");
        updateStatus("Recording stopped");

        addLog("flushing final mp3buffer");
        int outputMp3buf = androidLame.flush(mp3buffer);
        addLog("flushed " + outputMp3buf + " bytes");

        if (outputMp3buf > 0) {
            try {
                addLog("writing final mp3buffer to outputstream");
                outputStream.write(mp3buffer, 0, outputMp3buf);
                addLog("closing output stream");
                outputStream.close();
                updateStatus("Output recording saved in " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addLog("releasing audio recorder");
        audioRecord.stop();
        audioRecord.release();

        addLog("closing android lame");
        androidLame.close();

        isRecording = false;
    }

    private void addLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logFragment.addLog(log);
            }
        });
    }

    private void updateStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(status);
            }
        });
    }
}
