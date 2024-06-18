package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class WeightedSquaredToDistance extends AbstractDownsamplingAlgorithm {

    public WeightedSquaredToDistance() {
    }

    int convertFragment(Bitmap image) {
        Log.d("WeightedSquaredToDistance", "Fragment size: w="+image.getWidth()+", h="+image.getHeight());
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
        float maxDist = (float)(centerW * centerW + centerH * centerH);
        float dist = (float)((centerW - measuredW)*(centerW - measuredW) + (centerH - measuredH)*(centerH - measuredH));
        return (1 - dist / maxDist);
    }
}
/*
symetric parab. weight = A * dist^2 + B.
assumption: width < height.
dmin = width * param1 / 2; dmax = width * param2 / 2
0 = A * dmax * dmax + B
1 = A * dmin * dmin + B => 1 = A*(dmin^2 - dmax^2) => A = 1 / (dmin^2 - dmax^2)
B = -A * dmax^2 / (dmin^2 - dmax^2) = A * dmax / (dmax^2 - dmin^2)
 */