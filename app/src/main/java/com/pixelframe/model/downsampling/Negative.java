package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.util.Log;

public class Negative implements SamplingAlgorithm {
    public Negative() {
    }

    public int convert(Bitmap image, int width, int height, int param1, int param2) {
        int color = image.getPixel(width/2, height /2);
        return (color & 0xFF000000) | (~color & 0x00FF0000) | (~color & 0x0000FF00) | (~color & 0x000000FF);
    }
}