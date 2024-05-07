package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.pixelframe.model.SamplingAlgorithm;

public class CentralPixel implements SamplingAlgorithm {
    public CentralPixel() {
        Log.d("SamplingAlgorithm", "Selected: CentralPixel");
    }
    public int convert(Bitmap image, int width, int height) {
        return image.getPixel(width/2, height/2);
    }
}
