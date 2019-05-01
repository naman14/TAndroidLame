/*
 * Copyright (C) 2016 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.tandroidlame;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;
import com.naman14.androidlame.WaveReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class EncodeActivity extends AppCompatActivity {

    private int PICKFILE_REQUEST_CODE = 123;

    TextView pickFile;
    BufferedOutputStream outputStream;
    WaveReader waveReader;
    Uri inputUri;

    private static final int OUTPUT_STREAM_BUFFER = 8192;

    LogFragment logFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        pickFile = (TextView) findViewById(R.id.pickFile);

        logFragment = new LogFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.log_container, logFragment).commit();

        Button btnPickFile = (Button) findViewById(R.id.btnPickFile);

        btnPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile();
            }
        });

        final Button encode = (Button) findViewById(R.id.encode);
        encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFile.setText("Encoding to mp3...");
                new Thread() {
                    @Override
                    public void run() {
                        encode();
                    }
                }.start();
            }
        });

    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String filename = data.getDataString();
                inputUri = data.getData();
                if (inputUri != null) {
                    pickFile.setText(getRealPathFromURI(inputUri));
                }
            }
        }
    }


    private void encode() {

        File input = new File(getRealPathFromURI(inputUri));
        final File output = new File(Environment.getExternalStorageDirectory() + "/testencode.mp3");

        int CHUNK_SIZE = 8192;

        addLog("Initialising wav reader");
        waveReader = new WaveReader(input);

        try {
            waveReader.openWave();
        } catch (IOException e) {
            e.printStackTrace();
        }

        addLog("Intitialising encoder");
        AndroidLame androidLame = new LameBuilder()
                .setInSampleRate(waveReader.getSampleRate())
                .setOutChannels(waveReader.getChannels())
                .setOutBitrate(128)
                .setOutSampleRate(waveReader.getSampleRate())
                .setQuality(5)
                .build();

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(output), OUTPUT_STREAM_BUFFER);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int bytesRead = 0;

        short[] buffer_l = new short[CHUNK_SIZE];
        short[] buffer_r = new short[CHUNK_SIZE];
        byte[] mp3Buf = new byte[CHUNK_SIZE];

        int channels = waveReader.getChannels();

        addLog("started encoding");
        while (true) {
            try {
                if (channels == 2) {

                    bytesRead = waveReader.read(buffer_l, buffer_r, CHUNK_SIZE);
                    addLog("bytes read=" + bytesRead);

                    if (bytesRead > 0) {

                        int bytesEncoded = 0;
                        bytesEncoded = androidLame.encode(buffer_l, buffer_r, bytesRead, mp3Buf);
                        addLog("bytes encoded=" + bytesEncoded);

                        if (bytesEncoded > 0) {
                            try {
                                addLog("writing mp3 buffer to outputstream with " + bytesEncoded + " bytes");
                                outputStream.write(mp3Buf, 0, bytesEncoded);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else break;
                } else {

                    bytesRead = waveReader.read(buffer_l, CHUNK_SIZE);
                    addLog("bytes read=" + bytesRead);

                    if (bytesRead > 0) {
                        int bytesEncoded = 0;

                        bytesEncoded = androidLame.encode(buffer_l, buffer_l, bytesRead, mp3Buf);
                        addLog("bytes encoded=" + bytesEncoded);

                        if (bytesEncoded > 0) {
                            try {
                                addLog("writing mp3 buffer to outputstream with " + bytesEncoded + " bytes");
                                outputStream.write(mp3Buf, 0, bytesEncoded);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addLog("flushing final mp3buffer");
        int outputMp3buf = androidLame.flush(mp3Buf);
        addLog("flushed " + outputMp3buf + " bytes");

        if (outputMp3buf > 0) {
            try {
                addLog("writing final mp3buffer to outputstream");
                outputStream.write(mp3Buf, 0, outputMp3buf);
                addLog("closing output stream");
                outputStream.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pickFile.setText("Output mp3 saved in" + output.getAbsolutePath());
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void addLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logFragment.addLog(log);
            }
        });
    }
}
