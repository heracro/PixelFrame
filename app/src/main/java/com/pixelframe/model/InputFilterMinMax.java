package com.pixelframe.model;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

public class InputFilterMinMax implements InputFilter {

    private final Context context;
    private final int min, max;

    public InputFilterMinMax(Context context, int min, int max) {
        this.min = min;
        this.max = max;
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String stringInput = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);
            int input = Integer.parseInt(stringInput);
            if (isInRange(input))
                return null;
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid input, please enter a number between " + min + " and " + max, Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    private boolean isInRange(int input) {
        return input >= min && input <= max;
    }

}