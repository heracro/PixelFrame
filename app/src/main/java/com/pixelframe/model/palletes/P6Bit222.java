package com.pixelframe.model.palletes;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.pixelframe.model.configuration.Configuration;

public class P6Bit222 extends AbstractPalette {

    public int changePixelColor(int pixel) {
        return Color.argb(
                Color.alpha(pixel),
                Color.red(pixel) & 0xC0,
                Color.green(pixel) & 0xC0,
                Color.blue(pixel) & 0xC0
        );
    }
}
