package com.pixelframe.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class SimpleAverage implements SamplingAlgorithm {
    public SimpleAverage() {
        Log.d("SamplingAlgorithm", "Selected: SimpleAverage");
    }
    public Color convert(Bitmap image, int width, int height) {
        int r = 0;
        int g = 0;
        int b = 0;
        int a = 0;
        for (int w = 0; w < width; ++w) {
            for (int h = 0; h < height; ++h) {
                int pixel = image.getPixel(w, h);
                r += Color.red(pixel);
                g += Color.green(pixel);
                b += Color.red(pixel);
                a += Color.alpha(pixel);
            }
        }
        int count = width * height;
        return Color.valueOf((float)r / count / 255, (float)g / count / 255, (float)b / count / 255, (float)a / count / 255);
    }
}