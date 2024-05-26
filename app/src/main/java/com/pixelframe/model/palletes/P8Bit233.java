package com.pixelframe.model.palletes;

import android.graphics.Color;

public class P8Bit233 implements Pallete {
    @Override
    public int colorize(int pixel) {
        return Color.argb(
                Color.alpha(pixel),
                Color.red(pixel) & 0xC0,
                Color.green(pixel) & 0xE0,
                Color.blue(pixel) & 0xE0
        );
    }
}
