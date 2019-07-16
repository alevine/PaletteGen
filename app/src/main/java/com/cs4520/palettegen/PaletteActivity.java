package com.cs4520.palettegen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

public class PaletteActivity extends AppCompatActivity {

    ImageView givenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        givenImage = findViewById(R.id.exampleImg);

        // Get intent and given path from the camera intent
        Intent intent = getIntent();
        String currentPhotoPath = Objects.requireNonNull(intent.getExtras()).getString("currentPhotoLocation");

        // Make a File and get its URI to set the ImageView's src to
        assert currentPhotoPath != null;
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);

        givenImage.setImageURI(contentUri);
    }
}
