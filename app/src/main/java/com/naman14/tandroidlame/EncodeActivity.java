package com.naman14.tandroidlame;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;
import com.naman14.androidlame.LameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by naman on 29/01/16.
 */
public class EncodeActivity extends AppCompatActivity {

    private int PICKFILE_REQUEST_CODE = 123;

    TextView pickFile;
    FileOutputStream outputStream;

    Uri inputUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        pickFile = (TextView) findViewById(R.id.pickFile);

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
        intent.setType("audio/mpeg");
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

        AndroidLame androidLame = new LameBuilder()
                .setInSampleRate(8000)
                .setOutChannels(2)
                .setOutBitrate(32)
                .setOutSampleRate(8000)
                .setQuality(9)
                .build();

        int bytesRead = 0;

        try {
            outputStream = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        short[] buffer_l = new short[CHUNK_SIZE];
        short[] buffer_r = new short[CHUNK_SIZE];
        byte[] mp3Buf = new byte[CHUNK_SIZE];

        int channels = LameUtils.getChannelNumber(input.getAbsolutePath());

        while (true) {
            try {

                if (channels == 2) {
                    bytesRead = LameUtils.read(input, buffer_l, buffer_r, CHUNK_SIZE);
                    if (bytesRead > 0) {

                        int bytesEncoded = 0;
                        bytesEncoded = androidLame.encode(buffer_l, buffer_r, bytesRead, mp3Buf);

                        if (bytesEncoded > 0) {
                            try {
                                outputStream.write(mp3Buf, 0, bytesEncoded);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else break;
                } else {
                    bytesRead = LameUtils.read(input, buffer_l, CHUNK_SIZE);
                    if (bytesRead > 0) {
                        int bytesEncoded = 0;
                        bytesEncoded = androidLame.encode(buffer_l, buffer_l, bytesRead, mp3Buf);

                        if (bytesEncoded > 0) {
                            try {
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

        int outputMp3buf = androidLame.flush(mp3Buf);

        if (outputMp3buf > 0) {
            try {

                outputStream.write(mp3Buf, 0, outputMp3buf);
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pickFile.setText("Output in" + output.getAbsolutePath());
            }
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
