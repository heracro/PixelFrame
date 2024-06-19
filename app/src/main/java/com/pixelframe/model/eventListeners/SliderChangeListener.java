package com.pixelframe.model.eventListeners;

import android.widget.EditText;
import android.widget.SeekBar;

import com.pixelframe.model.SliderParamUser;

public class SliderChangeListener implements SeekBar.OnSeekBarChangeListener {
    private final EditText editText;
    private final SliderParamUser sliderParamUser;
    public SliderChangeListener(EditText editText, SliderParamUser sliderParamUser) {
        this.editText = editText;
        this.sliderParamUser = sliderParamUser;
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
        if (sliderParamUser != null) {
            sliderParamUser.transform(seekBar.getProgress());
        }
    }
}
