package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.pixelframe.model.SamplingAlgorithm;

public class SimpleAverage implements SamplingAlgorithm {
    public SimpleAverage() {
        Log.d("SamplingAlgorithm", "Selected: SimpleAverage");
    }
    public int convert(Bitmap image, int width, int height) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        for (int w = 0; w < width; ++w) {
            for (int h = 0; h < height; ++h) {
                int pixel = image.getPixel(w, h);
                red += Color.red(pixel);
                green += Color.green(pixel);
                blue += Color.red(pixel);
                alpha += Color.alpha(pixel);
            }
        }
        int count = width * height;
        return Color.argb(
                alpha / count,
                red / count,
                green / count,
                blue / count);
    }
}