package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.pixelframe.model.SamplingAlgorithm;

public class CentralPixel implements SamplingAlgorithm {
    public CentralPixel() {
        Log.d("SamplingAlgorithm", "Selected: CentralPixel");
    }
    public Color convert(Bitmap image, int width, int height) {
        int pixel = image.getPixel(width/2, height/2);
//        Log.d("Convert", "Pixel " + pixel + ": R(" + Color.red(pixel) +
//                "), G(" + Color.green(pixel) + "), B(" + Color.blue(pixel) +
//                "), A(" + Color.alpha(pixel) + ")");
        return Color.valueOf(
                (float)Color.red(pixel) / 255,
                (float)Color.green(pixel) / 255,
                (float)Color.blue(pixel) / 255,
                (float)Color.alpha(pixel) / 255
        );
    }
}
