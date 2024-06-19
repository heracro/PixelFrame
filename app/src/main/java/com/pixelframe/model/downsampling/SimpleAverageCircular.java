package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class SimpleAverageCircular extends AbstractDownsamplingAlgorithm {

    /**
     * This algorithm takes circle in the center of fragment, where diameter defined with param1
     * as % of smaller fragment dimension, and counts arithmetic average of RGC channels
     * from each pixel. The greater area, the greater distortion.
     * @param image fragment to reduce to single pixel
     * @return single pixel
     */
    int convertFragment(Bitmap image) {
        Log.d("SimpleAverageCircular", "Fragment size: w="+image.getWidth()+", h="+image.getHeight());
        int radius = (Math.min(image.getHeight(), image.getWidth())) * param1 / 200;
        int minCol = image.getWidth() / 2 - radius;
        int maxCol = image.getWidth() / 2 + radius - 1;
        int minRow = image.getHeight() / 2 - radius;
        int maxRow = image.getHeight() / 2 + radius - 1;
        Log.d("SimpleAverageCircular", "Limits: {minCol="+minCol+", maxCol="+maxCol+", minRow="+minRow+", maxRow="+maxRow+"}");
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        int count = 0;
        for (int w = minCol; w <= maxCol; ++w) {
            for (int h = minRow; h <= maxRow; ++h) {
                int x = w - image.getWidth() / 2;
                int y = h - image.getHeight() / 2;
                if (x * x + y * y <= radius * radius) {
                    int pixel = image.getPixel(w, h);
                    red += Color.red(pixel);
                    green += Color.green(pixel);
                    blue += Color.red(pixel);
                    alpha += Color.alpha(pixel);
                    ++count;
                }
            }
        }
        return Color.argb(alpha / count, red / count,
                green / count, blue / count);
    }

}