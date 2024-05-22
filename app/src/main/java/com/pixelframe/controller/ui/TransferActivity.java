package com.pixelframe.controller.ui;

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
import com.pixelframe.eventListeners.BTSendButtonOnClickListener;
import com.pixelframe.eventListeners.EditTextChangeListener;
import com.pixelframe.eventListeners.LayoutDimensionsListener;
import com.pixelframe.eventListeners.SliderChangeListener;
import com.pixelframe.eventListeners.SlotButtonPressListener;
import com.pixelframe.model.Configuration;
import com.pixelframe.model.InputFilterMinMax;

import java.util.ArrayList;
import java.util.List;

public class TransferActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private LinearLayout slotButtonsContainer;
    private List<Button> slotButtons = new ArrayList<>();
    private int chosenSlot;
    private Bitmap image;
    private SeekBar timeSlider;
    private final Integer BRIGHTNESS_INITIAL_VALUE = 100;
    private final Integer TIME_INITIAL_VALUE = 10;
    private BTSendButtonOnClickListener sendButtonListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        initLayout();
        loadImage();
    }

    private void loadImage() {
        image = getIntent().getParcelableExtra("convertedImage");
        if (image != null) {
            ImageView imageView = findViewById(R.id.image);
            imageView.setImageBitmap(image);
        }
    }

    private void initLayout() {
        constraintLayout = findViewById(R.id.constraintLayout);
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
        slotButtonsContainer = findViewById(R.id.slot_buttons_container);
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
        //brightness
        SeekBar brightnessSlider = findViewById(R.id.brightness_slider);
        brightnessSlider.setMin(0);
        brightnessSlider.setMax(200);
        brightnessSlider.setProgress(BRIGHTNESS_INITIAL_VALUE);
        EditText brightnessInput = findViewById(R.id.brightness_input);
        brightnessInput.setFilters(new InputFilter[]{
                new InputFilterMinMax(0, 200)
        });
        brightnessInput.setText(String.valueOf(BRIGHTNESS_INITIAL_VALUE));
        brightnessInput.addTextChangedListener(
                new EditTextChangeListener(brightnessInput, brightnessSlider)
        );
        brightnessSlider.setOnSeekBarChangeListener(
                new SliderChangeListener(brightnessInput)
        );
        //time
        timeSlider = findViewById(R.id.time_slider);
        timeSlider.setMin(0);
        timeSlider.setMax(600);
        timeSlider.setProgress(TIME_INITIAL_VALUE);
        EditText timeEdit = findViewById(R.id.time_input);
        timeEdit.setFilters(new InputFilter[]{
                new InputFilterMinMax(0, 600)
        });
        timeEdit.setText(String.valueOf(TIME_INITIAL_VALUE));
        timeEdit.addTextChangedListener(
                new EditTextChangeListener(timeEdit, timeSlider)
        );
        timeSlider.setOnSeekBarChangeListener(
                new SliderChangeListener(timeEdit)
        );
    }

    private void initSendButton() {
        Button sendButton = findViewById(R.id.send_button);
        sendButtonListener = new BTSendButtonOnClickListener(this);
        sendButton.setOnClickListener(sendButtonListener);
    }

    /**
     * Listens to events realted to granting permissions and re-calls event listener like it was
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
        if (requestCode == BTSendButtonOnClickListener.REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, let user know and "auto-press" button again
                Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show();
                sendButtonListener.onPermissionsGranted();
            } else {
                // Permissions not granted, just go back to transfer view and let him rot in sorrow.
                Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show();
            }
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

    public void setChosenSlot(int chosenSlot) {
        this.chosenSlot = chosenSlot;
    }

    public float getTime() {
        return timeSlider.getProgress();
    }
}
