package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.util.Log;

public class CentralPixel implements SamplingAlgorithm {

    public CentralPixel() {
    }

    public int convert(Bitmap image, int width, int height, int param1, int param2) {
        return image.getPixel(width/2, height/2);
    }
}