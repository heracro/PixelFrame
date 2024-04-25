package com.pixelframe.model;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Container for methods helping to convert bitmap from a bitmap to
 * format specific for PicoFrame .pfb
 */
public class ImageConverter {
    public static final boolean PALETTE_8BIT = true;
    public static final boolean PALETTE_24BIT = false;
    private boolean palette;
    private SamplingAlgorithm algorithm;
    ImageConverter(SamplingAlgorithm algorithm, boolean palette) {
        this.algorithm = algorithm;
        this.palette = palette;
    }
    Bitmap convert(Bitmap image, int width, int height) {
        Bitmap result = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        width -= (width % 64);
        height -= (height % 64);
        //at this point i don't need to care about lost 1px wide (or high) stripes.
        int fragmentWidth = width / 64;
        int fragmentHeight = height / 64;
        for (int wIdx = 0; wIdx < 64; ++wIdx) {
            for (int hIdx = 0; hIdx < 64; ++hIdx) {
                Bitmap fragment = Bitmap.createBitmap(
                        image, wIdx * fragmentWidth, hIdx * fragmentHeight,
                        fragmentWidth, fragmentHeight);
                result.setPixel(
                        wIdx, hIdx,
                        algorithm.convert(fragment, fragmentWidth, fragmentHeight).toArgb());
            }
        }
        return result;
    }


//myBitmap.setPixel(x, y, Color.rgb(45, 127, 0));
}
