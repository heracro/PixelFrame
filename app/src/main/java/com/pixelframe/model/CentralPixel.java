package com.pixelframe.model;

import android.graphics.Bitmap;
import android.graphics.Color;

public class CentralPixel implements SamplingAlgorithm {
    public Color convert(Bitmap image, int width, int height) {

        return Color.valueOf(255, 255, 255, 255);
    }
}
