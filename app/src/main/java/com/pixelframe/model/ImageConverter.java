package com.pixelframe.model;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Container for methods helping to convert bitmap from a bitmap to
 * size specific for PicoFrame
 */
public class ImageConverter {
    public static final boolean PALETTE_8BIT = true;
    public static final boolean PALETTE_24BIT = false;
    private boolean palette;
    private SamplingAlgorithm algorithm;
    public ImageConverter() {
    }
    public void setAlgorithm(SamplingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    public void setPalette(boolean palette) {
        this.palette = palette;
    }
    public Bitmap convert(Bitmap image, int width, int height) {
        Bitmap result = Bitmap.createBitmap(Configuration.MATRIX_WIDTH, Configuration.MATRIX_HEIGHT, Bitmap.Config.ARGB_8888);
        width -= (width % Configuration.MATRIX_WIDTH);
        height -= (height % Configuration.MATRIX_HEIGHT);
        //at this point i don't need to care about lost 1px wide (or high) stripes.
        int fragmentWidth = width / Configuration.MATRIX_WIDTH;
        int fragmentHeight = height / Configuration.MATRIX_HEIGHT;
        for (int column = 0; column < Configuration.MATRIX_WIDTH; ++column) {
            for (int row = 0; row < Configuration.MATRIX_HEIGHT; ++row) {
                Bitmap fragment = Bitmap.createBitmap(
                        image, column * fragmentWidth, row * fragmentHeight,
                        fragmentWidth, fragmentHeight);
                int color = algorithm.convert(fragment, fragmentWidth, fragmentHeight).toArgb();
                result.setPixel(column, row, color);
            }
        }
        return result;
    }
}
