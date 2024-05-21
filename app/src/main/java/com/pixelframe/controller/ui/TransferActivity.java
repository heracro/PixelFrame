package com.pixelframe.controller.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pixelframe.controller.R;
import com.pixelframe.eventListeners.LayoutDimensionsListener;
import com.pixelframe.eventListeners.SlotButtonPressListener;
import com.pixelframe.model.Configuration;

import java.util.ArrayList;
import java.util.List;

public class TransferActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private LinearLayout slotButtonsContainer;
    private List<Button> slotButtons = new ArrayList<>();
    private int chosenSlot;
    private Bitmap image;
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

    void initLayout() {
        constraintLayout = findViewById(R.id.constraintLayout);
        LayoutDimensionsListener layoutDimensionsListener = new LayoutDimensionsListener(
                constraintLayout,
                Configuration.TRANSFER_VIEW_FIRST_BLOCK_SIZE,
                Configuration.TRANSFER_VIEW_IMG_WIDTH
        );
        constraintLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(layoutDimensionsListener);
        addSlotButtons();
        addSliders();
    }

    private void addSlotButtons() {
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

    private void addSliders() {

    }

    public List<Button> getSlotButtons() {
        return slotButtons;
    }

    public void setChosenSlot(int chosenSlot) {
        this.chosenSlot = chosenSlot;
    }
}
