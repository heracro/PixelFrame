package com.pixelframe.model.palletes;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.pixelframe.model.configuration.Configuration;

public class P16Bit565 extends AbstractPalette {

    public int changePixelColor(int pixel) {
        return Color.argb(
                Color.alpha(pixel),
                Color.red(pixel) & 0xF8,
                Color.green(pixel) & 0xFC,
                Color.blue(pixel) & 0xF8
        );
    }
}
