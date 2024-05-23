package com.pixelframe.model.eventListeners.eventListeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;

import com.pixelframe.model.SliderParamUser;


public class EditTextChangeListener implements TextWatcher {
    private EditText editText;
    private SeekBar seekBar;
    SliderParamUser sliderParamUser;

    public EditTextChangeListener(EditText editText, SeekBar seekBar, SliderParamUser sliderParamUser) {
        this.editText = editText;
        this.seekBar = seekBar;
        this.sliderParamUser = sliderParamUser;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().isEmpty()) return;
        int parsed = Integer.parseInt(s.toString());
        seekBar.setProgress(parsed);
        editText.setSelection(s.length());
        if (sliderParamUser != null) {
            sliderParamUser.transform(parsed);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
