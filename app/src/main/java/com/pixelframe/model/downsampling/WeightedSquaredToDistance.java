package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class WeightedSquaredToDistance implements SamplingAlgorithm {

    public WeightedSquaredToDistance() {
    }

    public int convert(Bitmap image, int width, int height, int param1, int param2) {
        float totalWeight = 0;
        float red = 0;
        float green = 0;
        float blue = 0;
        float alpha = 0;
        for (int w = 0; w < width; ++w) {
            for (int h = 0; h < height; ++h) {
                int pixel = image.getPixel(w, h);
                float weight = weight(width / 2, height / 2, w, h);
                if (weight > 0) {
                    red += weight * Color.red(pixel);
                    green += weight * Color.green(pixel);
                    blue += weight * Color.red(pixel);
                    alpha += weight * Color.alpha(pixel);
                    totalWeight += weight;
                }
            }
        }
        return Color.argb(
                alpha / totalWeight,
                red / totalWeight,
                green / totalWeight,
                blue / totalWeight

        );
    }

    float weight(int centerW, int centerH, int measuredW, int measuredH) {
        float maxDist = (float)(centerW * centerW + centerH * centerH);
        float dist = (float)((centerW - measuredW)*(centerW - measuredW) + (centerH - measuredH)*(centerH - measuredH));
        return (1 - dist / maxDist);
    }
}