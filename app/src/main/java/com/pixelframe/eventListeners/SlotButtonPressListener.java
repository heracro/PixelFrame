package com.pixelframe.eventListeners;

import android.view.View;
import android.widget.Button;
import com.pixelframe.controller.ui.TransferActivity;

public class SlotButtonPressListener implements View.OnClickListener {
    TransferActivity activity;
    private final int slotNumber;
    public SlotButtonPressListener(TransferActivity activity, int slotNumber) {
        this.activity = activity;
        this.slotNumber = slotNumber;
    }
    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        for (Button b : activity.getSlotButtons()) {
            b.setPressed(b == clickedButton);
        }
        activity.setChosenSlot(slotNumber);
    }
}
