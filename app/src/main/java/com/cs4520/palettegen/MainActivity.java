package com.cs4520.palettegen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button cameraButton;
    Button uploadButton;
    ImageButton uploadFromURLButton;
    EditText uploadFromURLEditText;
    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_IMAGE = 2;
    static final int REQUEST_URL_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.cameraButton);
        uploadButton = findViewById(R.id.uploadButton);
        uploadFromURLButton = findViewById(R.id.urlUploadButton);
        uploadFromURLEditText = findViewById(R.id.urlUpload);

        // Add onClick listener for the camera button
        // Uses an Intent to start the external camera Activity
        // Saves the taken picture to a file and displays it in the following Activity
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;

                    try {
                        photoFile = createImageFile();
                    } catch (IOException ignored) {
                        // TODO: Handle this exception
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        // Use FileProvider to get the Uri for the created empty file
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                "com.cs4520.palettegen.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }

                }
            }
        });

        // Add onClick listener for the upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
            }
        });

        // Add onClick listener for the upload through URL button
        uploadFromURLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadURLImageTask task = new DownloadURLImageTask();
                task.execute(uploadFromURLEditText.getText().toString());
            }
        });
    }

    /**
     * Attribution: Obtained from the official Google Android documentation website.
     *
     * https://developer.android.com/training/camera/photobasics
     *
     * Modified slightly for our purposes.
     */
    private File createImageFile() throws IOException {
        // Create an image file name using the current DateTime
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "PalettePicture" + timeStamp;

        // Get external picture files directory, no need for write permissions
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create the temp file for saving
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Use onActivityResult to wait for the photo selection Intents to finish.
     * When they are completed we move to the next Activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent moveToPaletteIntent = new Intent(MainActivity.this, PaletteActivity.class);

            moveToPaletteIntent.putExtra("currentPhotoLocation", currentPhotoPath);
            startActivity(moveToPaletteIntent);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Intent moveToPaletteIntent = new Intent(MainActivity.this, PaletteActivity.class);

            moveToPaletteIntent.putExtra("currentPhotoLocation", currentPhotoPath);
            startActivity(moveToPaletteIntent);
        } else if (requestCode == REQUEST_URL_IMAGE && resultCode == RESULT_OK) {
            Intent moveToPaletteIntent = new Intent(MainActivity.this, PaletteActivity.class);

            moveToPaletteIntent.putExtra("currentPhotoLocation", currentPhotoPath);
            startActivity(moveToPaletteIntent);
        }
    }

    /**
     * This AsyncTask is used to download an image from url and then move directly to PaletteActivity
     */
    private class DownloadURLImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bm = null;
            // Need to grab file from url
            try {
                InputStream in = new URL(url).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                // If the user enters a bad url we need to let them know
                Toast toast = Toast.makeText(getApplicationContext(), "Bad URL", Toast.LENGTH_SHORT);
                toast.show();
            } catch (IOException e) {
                Log.e("DownloadURLImageTask failed - IO open failed", e.getMessage(), e);
            }

            // Need to create a temporary location for the downloaded image
            File imageFile = null;
            try {
                imageFile = createImageFile();
            }
            catch (IOException e) {
                Log.e("DownloadURLImageTask failed - could not create file", e.getMessage(), e);
            }

            // Continue only if the file was created
            if(imageFile != null) {
                try {
                    FileOutputStream out = new FileOutputStream(imageFile);
                    // Should have no effect on quality
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    // Deliberately call OnActivityResult in case new handling is introduced there
                    onActivityResult(REQUEST_URL_IMAGE, RESULT_OK, null);
                } catch (FileNotFoundException e) {
                    Log.e("DownloadURLImageTask failed - file not found", e.getMessage(), e);
                } catch (IOException e) {
                    Log.e("DownloadURLImageTask failed - IO flush or close failed", e.getMessage(), e);
                }
            }
            return bm;
        }
    }

}
