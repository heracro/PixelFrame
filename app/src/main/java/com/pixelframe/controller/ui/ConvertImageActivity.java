package com.pixelframe.controller.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pixelframe.controller.R;
import com.pixelframe.eventListeners.AlgorithmItemSelectedListener;
import com.pixelframe.eventListeners.PreviewButtonOnClickListener;
import com.pixelframe.eventListeners.SendButtonOnClickListener;
import com.pixelframe.model.Configuration;
import com.pixelframe.model.ImageConverter;
import com.pixelframe.eventListeners.PaletteItemSelectedListener;

public class ConvertImageActivity extends AppCompatActivity {
    private ImageView sourceView;
    private Bitmap sourceBitmap;
    private ImageView resultView;
    private Bitmap resultBitmap;
    private Spinner paletteSpinner;
    private Spinner algorithmSpinner;
    private ImageConverter imageConverter;
    private Button previewButton;
    private Button sendButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        sourceView = findViewById(R.id.source_image);
        resultView = findViewById(R.id.result_image);
        paletteSpinner = findViewById(R.id.palette_spinner);
        algorithmSpinner = findViewById(R.id.algorithm_spinner);
        previewButton = findViewById(R.id.button_preview);
        sendButton = findViewById(R.id.button_send);
        imageConverter = new ImageConverter();

        //set spinners' content
        ArrayAdapter<String> paletteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Configuration.PALETTE_SPINNER_CHOICES);
        paletteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paletteSpinner.setAdapter(paletteAdapter);
        paletteSpinner.setOnItemSelectedListener(
                new PaletteItemSelectedListener(imageConverter)
        );

        ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Configuration.ALGORITHM_SPINNER_CHOICES);
        algorithmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algorithmSpinner.setAdapter(algorithmAdapter);
        algorithmSpinner.setOnItemSelectedListener(
                new AlgorithmItemSelectedListener(imageConverter)
        );

        //load image and size
        String imagePath = getIntent().getStringExtra("imagePath");
        int imageWidth = getIntent().getIntExtra("width", 0);
        int imageHeight = getIntent().getIntExtra("height", 0);
        //fill in source image frame
        sourceBitmap = BitmapFactory.decodeFile(imagePath);
        if (sourceBitmap == null) {
            Toast.makeText(this, "Failed to load picture fragment, clear cache and try again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConvertImageActivity.this, MainActivity.class);
            startActivity(intent);
        }
        sourceView.setImageBitmap(sourceBitmap);
        //set buttons and their handlers
        previewButton.setOnClickListener(
                new PreviewButtonOnClickListener(resultView, imageConverter, sourceBitmap, resultBitmap, imageWidth, imageHeight)
        );
        sendButton.setOnClickListener(
                new SendButtonOnClickListener() //This would change during implementation of listener's class
        );
    }
}