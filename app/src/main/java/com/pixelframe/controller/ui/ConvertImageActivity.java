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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pixelframe.controller.R;
import com.pixelframe.eventListeners.AlgorithmItemSelectedListener;
import com.pixelframe.eventListeners.LayoutDimensionsListener;
import com.pixelframe.eventListeners.PreviewButtonOnClickListener;
import com.pixelframe.eventListeners.SendButtonOnClickListener;
import com.pixelframe.model.Configuration;
import com.pixelframe.model.ImageConverter;
import com.pixelframe.eventListeners.PaletteItemSelectedListener;

public class ConvertImageActivity extends AppCompatActivity {
    private int imageWidth;
    private int imageHeight;
    private final ImageConverter imageConverter = new ImageConverter();
    private ImageView resultView;
    private Bitmap chosenFragment;
    private Bitmap convertedFragment;
    private Bitmap simulatedFragmentLook;
    ConstraintLayout constraintLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        initLayout();
        loadImage();
    }
    public int getImageWidth() {
        return imageWidth;
    }
    public int getImageHeight() {
        return imageHeight;
    }
    public Bitmap getChosenFragment() {
        return chosenFragment;
    }
    public ImageConverter getImageConverter() {
        return imageConverter;
    }
    public Bitmap getConvertedFragment() {
        return convertedFragment;
    }
    public void setConvertedFragment(Bitmap fragment) {
        convertedFragment = fragment;
    }
    public void setSimulatedFragmentLook(Bitmap fragment) {
        simulatedFragmentLook = fragment;
    }

    public void refreshResultView() {
        resultView.setImageBitmap(simulatedFragmentLook);
    }

    private void initLayout() {
        initLayoutConstraints();
        initSpinners();
        initTextInputs();
        initButtons();
    }

    private void initLayoutConstraints() {
        constraintLayout = findViewById(R.id.converter_main);
        LayoutDimensionsListener layoutDimensionsListener = new LayoutDimensionsListener(
                constraintLayout,
                Configuration.CONVERT_VIEW_FIRST_BLOCK_SIZE,
                Configuration.CONVERT_VIEW_IMG_WIDTH
        );
        constraintLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(layoutDimensionsListener);
    }

    private void initSpinners() {
        Spinner paletteSpinner = findViewById(R.id.palette_spinner);
        Spinner algorithmSpinner = findViewById(R.id.algorithm_spinner);
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
    }

    private void initButtons() {
        Button previewButton = findViewById(R.id.button_preview);
        Button sendButton = findViewById(R.id.button_send);
        previewButton.setOnClickListener(
                new PreviewButtonOnClickListener(this)
        );
        sendButton.setOnClickListener(
                new SendButtonOnClickListener(this)
        );
    }

    private void initTextInputs() {

    }

    private void loadImage() {
        resultView = findViewById(R.id.source_image);
        String imagePath = getIntent().getStringExtra("imagePath");
        imageWidth = getIntent().getIntExtra("width", 0);
        imageHeight = getIntent().getIntExtra("height", 0);
        chosenFragment = BitmapFactory.decodeFile(imagePath);
        if (chosenFragment == null) {
            Toast.makeText(this, "Failed to load picture fragment, clear cache and try again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ConvertImageActivity.this, MainActivity.class);
            startActivity(intent);
        }
        resultView.setImageBitmap(chosenFragment);
    }
}