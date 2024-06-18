package com.pixelframe.model.palletes;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.pixelframe.model.configuration.Configuration;

public class P8Bit233 extends AbstractPalette {

    public int changePixelColor(int pixel) {
        return Color.argb(
                Color.alpha(pixel),
                Color.red(pixel) & 0xC0,
                Color.green(pixel) & 0xE0,
                Color.blue(pixel) & 0xE0
        );
    }
}
