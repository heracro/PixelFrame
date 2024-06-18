package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class SimpleAverageLimitedRectangle extends AbstractDownsamplingAlgorithm {

    /**
     * This algorithm takes rectangle in the center of fragment, defined with param1 as % of width
     * and param2 as % of height, and counts arithmetic average of RGC channels from each pixel.
     * The greater area, the greater distortion.
     * @param image fragment to reduce to single pixel
     * @return single pixel
     */
    int convertFragment(Bitmap image) {
        Log.d("SimpleAverageLimitedRectangle", "Fragment size: w="+image.getWidth()+", h="+image.getHeight());
        int firstCol = image.getWidth() * (int)(0.5 - param1 / 200f);
        int lastCol = Math.max(image.getWidth() * (int)(0.5 + param1 / 200f) - 1, 0);
        int firstRow = image.getHeight() * (int)(0.5 - param2 / 200f);
        int lastRow = Math.max(image.getHeight() * (int)(0.5 + param2 / 200f) - 1, 0);
        Log.d("SimpleAverageLimitedRectangle", "Limits: {firstCol="+firstCol+", lastCol="+lastCol+", firstRow="+firstRow+", lastRow="+lastRow+"}");
        int alpha = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int pixelCount = 0;
        for (int c = firstCol; c <= lastCol; ++c) {
            for (int r = firstRow; r <= lastRow; ++r) {
                int pixel = image.getPixel(c, r);
                ++pixelCount;
                alpha += Color.alpha(pixel);
                red += Color.red(pixel);
                green += Color.green(pixel);
                blue += Color.blue(pixel);
            }
        }
        return Color.argb(alpha / pixelCount, red / pixelCount,
                green / pixelCount, blue / pixelCount);
    }


}