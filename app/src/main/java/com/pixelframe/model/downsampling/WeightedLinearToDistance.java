package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class WeightedLinearToDistance extends AbstractDownsamplingAlgorithm {

    float maxDist;

    public WeightedLinearToDistance() {
    }

    int convertFragment(Bitmap image) {
        maxDist = (float)Math.sqrt(2) * image.getWidth();
        int center = image.getHeight() / 2;
        float totalWeight = 0;
        float red = 0;
        float green = 0;
        float blue = 0;
        float alpha = 0;
        for (int w = 0; w < image.getWidth(); ++w) {
            for (int h = 0; h < image.getHeight(); ++h) {
                int pixel = image.getPixel(w, h);
                float weight = weight(center, center, w, h);
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

        float dist = (float)Math.sqrt((centerW - measuredW)*(centerW - measuredW) + (centerH - measuredH)*(centerH - measuredH));
        return (1 - dist / maxDist);
    }
}