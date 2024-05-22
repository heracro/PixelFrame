package com.pixelframe.eventListeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;


public class EditTextChangeListener implements TextWatcher {
    private EditText editText;
    private SeekBar seekBar;

    public EditTextChangeListener(EditText editText, SeekBar seekBar) {
        this.editText = editText;
        this.seekBar = seekBar;
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
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
