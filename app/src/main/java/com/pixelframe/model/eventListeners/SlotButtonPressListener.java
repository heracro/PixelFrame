package com.pixelframe.model.eventListeners;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pixelframe.controller.ui.activity.TransferActivity;

public class SlotButtonPressListener implements View.OnClickListener {
    final TransferActivity activity;
    private final int slotNumber;
    public SlotButtonPressListener(TransferActivity activity, int slotNumber) {
        this.activity = activity;
        this.slotNumber = slotNumber;
    }
    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        for (Button b : activity.getSlotButtons()) {
            Log.d("SlotButtonPressListener", "Button " + b.getText() +
                    " set to " + ((b == clickedButton) ? "True" : "False"));
            b.setActivated(b == clickedButton);
            b.setPressed(b == clickedButton);
            b.setTextColor(Color.BLACK);
        }
        clickedButton.setTextColor(Color.RED);
        activity.setChosenSlot(slotNumber);
    }
}
