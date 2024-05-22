package com.pixelframe.eventListeners;

import android.widget.EditText;
import android.widget.SeekBar;

public class SliderChangeListener implements SeekBar.OnSeekBarChangeListener {
    private EditText editText;
    public SliderChangeListener(EditText editText) {
        this.editText = editText;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        editText.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
