package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class WeightedLimitedLinearToDistance extends AbstractDownsamplingAlgorithm {

    public WeightedLimitedLinearToDistance() {
    }

    public int convertFragment(Bitmap image) {
        float totalWeight = 0;
        float red = 0;
        float green = 0;
        float blue = 0;
        float alpha = 0;
        for (int w = 0; w < image.getWidth(); ++w) {
            for (int h = 0; h < image.getHeight(); ++h) {
                int pixel = image.getPixel(w, h);
                float weight = weight(image.getWidth() / 2, image.getHeight() / 2, w, h);
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
        float maxDist = (float)Math.sqrt(centerW * centerW);
        float dist = (float)Math.sqrt((centerW - measuredW)*(centerW - measuredW) + (centerH - measuredH)*(centerH - measuredH));
        return dist >= maxDist ? 0 : (1 - dist / maxDist);
    }
}