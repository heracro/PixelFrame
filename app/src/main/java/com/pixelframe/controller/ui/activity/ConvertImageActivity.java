package com.pixelframe.controller.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pixelframe.controller.R;
import com.pixelframe.model.downsampling.AbstractDownsamplingAlgorithm;
import com.pixelframe.model.downsampling.CentralPixel;
import com.pixelframe.model.eventListeners.AlgorithmItemSelectedListener;
import com.pixelframe.model.eventListeners.EditTextChangeListener;
import com.pixelframe.model.eventListeners.LayoutDimensionsListener;
import com.pixelframe.model.eventListeners.PreviewButtonOnClickListener;
import com.pixelframe.model.eventListeners.SendButtonOnClickListener;
import com.pixelframe.model.eventListeners.SliderChangeListener;
import com.pixelframe.model.configuration.Configuration;
import com.pixelframe.model.eventListeners.PaletteItemSelectedListener;
import com.pixelframe.model.filters.InputFilterMinMax;
import com.pixelframe.model.palletes.AbstractPalette;
import com.pixelframe.model.palletes.SourcePalette;

public class ConvertImageActivity extends AppCompatActivity {
    private int imageWidth;
    private int imageHeight;
    private Class<? extends AbstractDownsamplingAlgorithm> downsampler;
    private Class<? extends AbstractPalette> palette;
    private ImageView resultView;
    private Bitmap chosenFragment;
    private Bitmap convertedFragment;
    private Bitmap simulatedFragmentLook;
    private Button sendButton;
    private int algorithmPosition;
    private SeekBar sliderParam1;
    private EditText editParam1;
    private SeekBar sliderParam2;
    private EditText editParam2;
    private String param1Hint;
    private String param2Hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        downsampler = CentralPixel.class;
        palette = SourcePalette.class;
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

    public Class<? extends AbstractDownsamplingAlgorithm> getDownsamplerClass() {
        return downsampler;
    }

    public void setDownsamplerClass(Class<? extends AbstractDownsamplingAlgorithm> downsampler) {
        this.downsampler = downsampler;
    }

    public Class<? extends AbstractPalette> getPaletteClass() {
        return palette;
    }

    public void setPaletteClass(Class<? extends AbstractPalette> palette) {
        this.palette = palette;
    }

    public int getParam1Value() {
        return sliderParam1.getProgress();
    }

    public int getParam2Value() {
        return sliderParam2.getProgress();
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
        initSliders();
        initButtons();
    }

    private void initLayoutConstraints() {
        ConstraintLayout constraintLayout = findViewById(R.id.converter_main);
        LayoutDimensionsListener layoutDimensionsListener = new LayoutDimensionsListener(
                constraintLayout,
                Configuration.CONVERT_VIEW_FIRST_BLOCK_SIZE,
                Configuration.CONVERT_VIEW_IMG_WIDTH
        );
        constraintLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(layoutDimensionsListener);
    }

    private void initSpinners() {
        // Palette
        Spinner paletteSpinner = findViewById(R.id.palette_spinner);
        String[] paletteNames = new String[Configuration.PALETTES.length];
        for (int i = 0; i < Configuration.PALETTES.length; ++i) {
            paletteNames[i] = Configuration.PALETTES[i].name;
        }
        ArrayAdapter<String> paletteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                paletteNames);
        paletteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paletteSpinner.setAdapter(paletteAdapter);
        paletteSpinner.setOnItemSelectedListener(
                new PaletteItemSelectedListener(this)
        );
        // Algorithm
        Spinner algorithmSpinner = findViewById(R.id.algorithm_spinner);
        String[] algorithmNames = new String[Configuration.ALGORITHMS.length];
        for (int i = 0; i < Configuration.ALGORITHMS.length; i++) {
            algorithmNames[i] = Configuration.ALGORITHMS[i].name;
        }
        ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                algorithmNames);
        algorithmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algorithmSpinner.setAdapter(algorithmAdapter);
        algorithmSpinner.setOnItemSelectedListener(
                new AlgorithmItemSelectedListener(this)
        );
    }

    private void initButtons() {
        Button previewButton = findViewById(R.id.button_preview);
        previewButton.setOnClickListener(
                new PreviewButtonOnClickListener(this)
        );
        sendButton = findViewById(R.id.button_send);
        enableSendButton(false);
        sendButton.setOnClickListener(
                new SendButtonOnClickListener(this)
        );
    }

    public void initSliders() {
        // Parameter 1
        sliderParam1 = findViewById(R.id.first_param_slider);
        sliderParam1.setMin(0);
        sliderParam1.setMax(100);
        Integer PARAM_1_INITIAL_VALUE = 50;
        sliderParam1.setProgress(PARAM_1_INITIAL_VALUE);
        editParam1 = findViewById(R.id.first_param_input);
        editParam1.setFilters(new InputFilter[]{
                new InputFilterMinMax(this, 0, 100)
        });
        editParam1.setText(String.valueOf(PARAM_1_INITIAL_VALUE));
        editParam1.setHint(param1Hint);
        editParam1.addTextChangedListener(
                new EditTextChangeListener(editParam1, sliderParam1, null)
        );
        sliderParam1.setOnSeekBarChangeListener(
                new SliderChangeListener(editParam1, null)
        );
        //Parameter 2
        sliderParam2 = findViewById(R.id.second_param_slider);
        sliderParam2.setMin(0);
        sliderParam2.setMax(100);
        Integer PARAM_2_INITIAL_VALUE = 50;
        sliderParam2.setProgress(PARAM_2_INITIAL_VALUE);
        editParam2 = findViewById(R.id.second_param_input);
        editParam2.setFilters(new InputFilter[]{
                new InputFilterMinMax(this, 0, 100)
        });
        editParam2.setText(String.valueOf(PARAM_2_INITIAL_VALUE));
        editParam2.setHint(param2Hint);
        editParam2.addTextChangedListener(
                new EditTextChangeListener(editParam2, sliderParam2, null)
        );
        sliderParam2.setOnSeekBarChangeListener(
                new SliderChangeListener(editParam2, null)
        );
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

    public void enableSendButton(boolean enable) {
        sendButton.setEnabled(enable);
        sendButton.setAlpha(enable ? 1f : 0.7f);
    }

    public void setParameterControlsEnabled(int count) {
        if (count == 0) {
            enableSlider(sliderParam1, false);
            enableSlider(sliderParam2, false);
            enableEditText(editParam1, false);
            enableEditText(editParam2, false);
        } else if (count == 1) {
            enableSlider(sliderParam1, true);
            enableSlider(sliderParam2, false);
            enableEditText(editParam1, true);
            enableEditText(editParam2, false);
        } else {
            enableSlider(sliderParam1, true);
            enableSlider(sliderParam2, true);
            enableEditText(editParam1, true);
            enableEditText(editParam2, true);
        }
    }

    private void enableSlider(SeekBar seekBar, boolean enable) {
        seekBar.setEnabled(enable);
        seekBar.setAlpha(enable ? 1f : 0.7f);
    }

    private void enableEditText(EditText editText, boolean enable) {
        editText.setEnabled(enable);
        editText.setAlpha(enable ? 1f : 0.7f);
    }

    public void setAlgorithmPosition(int position) {
        this.algorithmPosition = position;
    }

    public void updateParameterHints(int position) {
        param1Hint = Configuration.ALGORITHMS[position].parameterHint1;
        param2Hint = Configuration.ALGORITHMS[position].parameterHint2;
    }
}