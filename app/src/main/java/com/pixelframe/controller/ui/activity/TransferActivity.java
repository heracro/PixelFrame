package com.pixelframe.controller.ui.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pixelframe.controller.R;
import com.pixelframe.model.configuration.Configuration;
import com.pixelframe.model.eventListeners.EditTextChangeListener;
import com.pixelframe.model.eventListeners.LayoutDimensionsListener;
import com.pixelframe.model.eventListeners.SendUsingWifiButtonListener;
import com.pixelframe.model.eventListeners.SliderChangeListener;
import com.pixelframe.model.eventListeners.SlotButtonPressListener;
import com.pixelframe.model.filters.InputFilterMinMax;
import com.pixelframe.model.processing.LinearBrightnessAdjuster;
import com.pixelframe.model.processing.MatrixLikeResultView;

import java.util.ArrayList;
import java.util.List;

public class TransferActivity extends AppCompatActivity {

    private final List<Button> slotButtons = new ArrayList<>();
    private int chosenSlot;
    private Bitmap image;
    private ImageView imageView;
    private SeekBar timeSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        loadImage();
        initLayout();
    }

    private void loadImage() {
        image = getIntent().getParcelableExtra("convertedImage");
        if (image != null) {
            imageView = findViewById(R.id.image);
            imageView.setImageBitmap(MatrixLikeResultView.convert(image));
        }
    }

    private void initLayout() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        LayoutDimensionsListener layoutDimensionsListener = new LayoutDimensionsListener(
                constraintLayout,
                Configuration.TRANSFER_VIEW_FIRST_BLOCK_SIZE,
                Configuration.TRANSFER_VIEW_IMG_WIDTH
        );
        constraintLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(layoutDimensionsListener);
        initSlotButtons();
        initSliders();
        initSendButton();
    }

    private void initSlotButtons() {
        LinearLayout slotButtonsContainer = findViewById(R.id.slot_buttons_container);
        Context context = slotButtonsContainer.getContext();
        int count = Configuration.SLOT_BUTTON_COUNT;
        for (int i = 1; i <= count; i++) {
            Button slotButton = new Button(context);
            slotButton.setId(View.generateViewId());
            slotButton.setText(String.valueOf(i));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            layoutParams.setMarginStart(8);
            layoutParams.setMarginEnd(i != count ? 8 : 16);
            slotButton.setLayoutParams(layoutParams);
            slotButton.setOnClickListener(
                    new SlotButtonPressListener(this, i));
            slotButtonsContainer.addView(slotButton);
            slotButtons.add(slotButton);
        }
    }

    private void initSliders() {
        int brightness_initial = Configuration.BRIGHTNESS_MIN + (Configuration.BRIGHTNESS_MAX - Configuration.BRIGHTNESS_MIN)/2;
        int time_initial = Configuration.TIME_MIN + (Configuration.TIME_MAX - Configuration.TIME_MIN) / 2;
        //brightness
        SeekBar brightnessSlider = findViewById(R.id.brightness_slider);
        brightnessSlider.setMin(Configuration.BRIGHTNESS_MIN);
        brightnessSlider.setMax(Configuration.BRIGHTNESS_MAX);
        brightnessSlider.setProgress(brightness_initial);
        EditText brightnessInput = findViewById(R.id.brightness_input);
        brightnessInput.setFilters(new InputFilter[]{
                new InputFilterMinMax(this, Configuration.BRIGHTNESS_MIN, Configuration.BRIGHTNESS_MAX)
        });
        brightnessInput.setText(String.valueOf(brightness_initial));
        LinearBrightnessAdjuster lba = new LinearBrightnessAdjuster(image, this::setAndRefreshPreview);
        brightnessInput.addTextChangedListener(
                new EditTextChangeListener(brightnessInput, brightnessSlider, lba)
        );
        brightnessSlider.setOnSeekBarChangeListener(
                new SliderChangeListener(brightnessInput, lba)
        );
        //time
        timeSlider = findViewById(R.id.time_slider);
        timeSlider.setMin(Configuration.TIME_MIN);
        timeSlider.setMax(Configuration.TIME_MAX);
        timeSlider.setProgress(time_initial);
        EditText timeEdit = findViewById(R.id.time_input);
        timeEdit.setFilters(new InputFilter[]{
                new InputFilterMinMax(this, Configuration.TIME_MIN, Configuration.TIME_MAX)
        });
        timeEdit.setText(String.valueOf(time_initial));
        timeEdit.addTextChangedListener(
                new EditTextChangeListener(timeEdit, timeSlider, null)
        );
        timeSlider.setOnSeekBarChangeListener(
                new SliderChangeListener(timeEdit, null)
        );
    }

    private void initSendButton() {
        Button sendButton = findViewById(R.id.send_button);
        SendUsingWifiButtonListener sendButtonListener = new SendUsingWifiButtonListener(this);
        sendButton.setOnClickListener(sendButtonListener);
    }

    /**
     * Listens to events related to granting permissions and re-calls event listener like it was
     * send-button pressed by user.
     * @param requestCode The request code passed in requestPermissions(
     * android.app.Activity, String[], int)
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions granted, press Transfer button again", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permissions are required for Bluetooth operations", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Button> getSlotButtons() {
        return slotButtons;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getChosenSlot() {
        return chosenSlot;
    }

    public void setChosenSlot(final int chosenSlot) {
        this.chosenSlot = chosenSlot;
    }

    public float getTime() {
        return timeSlider.getProgress();
    }

    public void setAndRefreshPreview(final Bitmap image) {
        imageView.setImageBitmap(MatrixLikeResultView.convert(image));
    }

    public void showToastMessage(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
