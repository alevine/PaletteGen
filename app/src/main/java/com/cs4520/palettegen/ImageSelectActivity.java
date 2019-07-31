package com.cs4520.palettegen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.cs4520.palettegen.db.PaletteContract;
import com.cs4520.palettegen.db.PaletteDbHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import utils.PaletteGenerator;

public class ImageSelectActivity extends AppCompatActivity {

    private EditText uploadFromURLEditText;
    private String currentPhotoPath;

    private static final int REQUEST_READ_PERMISSIONS = 0;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_PICK_IMAGE = 2;

    private static final int REQUEST_URL_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        Button cameraButton = findViewById(R.id.cameraButton);
        Button uploadButton = findViewById(R.id.uploadButton);
        ImageButton uploadFromURLButton = findViewById(R.id.urlUploadButton);
        uploadFromURLEditText = findViewById(R.id.urlUpload);

        // Add onClick listener for the camera button
        // Uses an Intent to start the external camera Activity
        // Saves the taken picture to a file and displays it in the following Activity
        cameraButton.setOnClickListener(view -> {

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
                    Uri photoURI = FileProvider.getUriForFile(ImageSelectActivity.this,
                            "com.cs4520.palettegen.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });

        // Add onClick listener for the upload button
        uploadButton.setOnClickListener(view -> {
            // Verify that all required contact permissions have been granted.
            if (ActivityCompat.checkSelfPermission(ImageSelectActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Contacts permissions have not been granted.
                Log.i("ImageSelectActivity",
                        "Read permissions has NOT been granted. Requesting permissions.");
                requestReadPermissions();
            } else {

                // Contact permissions have been granted. Show the contacts fragment.
                Log.i("ImageSelectActivity",
                        "Read permissions already granted.");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });

        // Add onClick listener for the upload through URL button
        uploadFromURLButton.setOnClickListener(view -> {
            DownloadURLImageTask task = new DownloadURLImageTask();
            task.execute(uploadFromURLEditText.getText().toString());
        });
    }

    /**
     * Requests the Read External Storage permissions.
     * If the permission has been denied previously, a SnackBar (yum!) will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ImageSelectActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("ImageSelectActivity",
                    "Displaying read permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(findViewById(R.id.imageSelectLayout), "PaletteTown needs your permission to read external storage.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Confirmed", view -> ActivityCompat
                            .requestPermissions(ImageSelectActivity.this,
                                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                                    REQUEST_READ_PERMISSIONS))
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(ImageSelectActivity.this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_READ_PERMISSIONS);
        }
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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        final PaletteGenerator generator = new PaletteGenerator();
        final Intent replyIntent = new Intent();

        PaletteDbHelper dbHelper = new PaletteDbHelper(ImageSelectActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Use database utils to query number of entries in the palette table
        int count = (int) DatabaseUtils.queryNumEntries(db, PaletteContract.PaletteEntry.TABLE_NAME) + 1;

        // Based on requestCode, get the relevant content to create the palette and pass
        // it back to the main activity
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_URL_IMAGE)
                && resultCode == RESULT_OK) {
            replyIntent.putExtra("colorString",
                    generator.generatePaletteColorsFromPath(currentPhotoPath));
            replyIntent.putExtra("paletteName", "Palette #" + count);
            setResult(RESULT_OK, replyIntent);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            replyIntent.putExtra("colorString",
                    generator.generatePaletteColorsFromPath(picturePath));
            replyIntent.putExtra("paletteName", "Palette #" + count);
            setResult(RESULT_OK, replyIntent);
        }

        finish();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // TODO: Don't make them click twice
        if (requestCode == REQUEST_READ_PERMISSIONS) {
            // Received permission result for read external permission.
            Log.i("ImageSelectActivity", "Received response for read permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i("ImageSelectActivity", "READ permission has now been granted. Showing preview.");
                Snackbar.make(findViewById(R.id.imageSelectLayout), "Permission granted!",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i("ImageSelectActivity", "READ permission was NOT granted.");
                Snackbar.make(findViewById(R.id.imageSelectLayout), "Permission not granted.",
                        Snackbar.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * This AsyncTask is used to download an image from url and then move directly to PaletteActivity
     */
    private class DownloadURLImageTask extends AsyncTask<String, Void, Bitmap> {
        private int status = OK;

        private static final int OK = 1;

        private static final int IO_ERROR = 2;

        private static final int BAD_URL = 3;

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
                status = BAD_URL;
                return null;
            } catch (IOException e) {
                status = IO_ERROR;
                Log.e("DownloadURLImageTask failed - IO open failed", e.getMessage(), e);
            }

            // Need to create a temporary location for the downloaded image
            File imageFile = null;
            try {
                imageFile = createImageFile();
            }
            catch (IOException e) {
                status = IO_ERROR;
                Log.e("DownloadURLImageTask failed - could not create file", e.getMessage(), e);
            }

            // Continue only if the file was created and the bitmap downloaded
            if(imageFile != null && bm != null) {
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
                    status = IO_ERROR;
                    Log.e("DownloadURLImageTask failed - IO flush or close failed", e.getMessage(), e);
                }
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(status == DownloadURLImageTask.BAD_URL) {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid URL entered.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
