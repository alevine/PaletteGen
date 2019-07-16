package com.cs4520.palettegen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton cameraButton;
    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.cameraButton);

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
    }

    /**
     * Attribution: Obtained from the official Google Android documentation website.
     * <p>
     * https://developer.android.com/training/camera/photobasics
     * <p>
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
        }
    }



}
