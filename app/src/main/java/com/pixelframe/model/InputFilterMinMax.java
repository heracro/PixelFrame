package com.pixelframe.model;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private final int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String stringInput = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);
            int input = Integer.parseInt(stringInput);
            if (isInRange(input))
                return null;
        } catch (NumberFormatException e) {
        }
        return "";
    }

    private boolean isInRange(int input) {
        return input >= min && input <= max;
    }
}
