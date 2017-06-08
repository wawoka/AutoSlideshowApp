package jp.atsushitechacademy.kanamori.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Timer mTimer;
    double mTimerSec = 0.0;
    Handler mHandler = new Handler();
    int a = 1;

    ContentResolver resolver;
    Cursor cursor;

    Button mNextButton;
    Button mPsButton;
    Button mReturnButton;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNextButton = (Button) findViewById(R.id.next_button);
        mPsButton = (Button) findViewById(R.id.ps_button);
        mReturnButton = (Button) findViewById(R.id.return_button);

        mNextButton.setOnClickListener(this);
        mPsButton.setOnClickListener(this);
        mReturnButton.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                } else {
                    mNextButton.setEnabled(false);
                    mPsButton.setEnabled(false);
                    mReturnButton.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_button) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            cursor.moveToNext();
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
            if (cursor.isLast()) {
                cursor.moveToFirst();
            }

        } else if (v.getId() == R.id.return_button) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (cursor.isFirst()) {
                cursor.moveToLast();
            } else if (cursor.moveToPrevious()) {


                //cursor.moveToPrevious();
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                imageVIew.setImageURI(imageUri);
            }

        } else if (v.getId() == R.id.ps_button) {

            switch (a) {
                case 1:
                    mNextButton.setEnabled(false);
                    mReturnButton.setEnabled(false);
                    a = 2;
                    break;
                case 2:
                    mNextButton.setEnabled(true);
                    mReturnButton.setEnabled(true);
                    a = 1;
                    break;
                default:
                    break;
            }

            if (mTimer == null) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimerSec += 0.1;

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                cursor.moveToNext();
                                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                Long id = cursor.getLong(fieldIndex);
                                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                                ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                                imageVIew.setImageURI(imageUri);
                                if (cursor.isLast()) {
                                    cursor.moveToFirst();
                                }
                            }
                        });
                    }
                }, 2000, 2000);
            } else if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }



    private void getContentsInfo() {

        resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Android", "onStop");
        cursor.close();

    }
}
