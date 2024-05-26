package com.pixelframe.model.palletes;

import android.graphics.Color;

public class P16Bit565 implements Pallete {
    @Override
    public int colorize(int pixel) {
        return Color.argb(
                Color.alpha(pixel),
                Color.red(pixel) & 0xF8,
                Color.green(pixel) & 0xFC,
                Color.blue(pixel) & 0xF8
        );
    }
}
