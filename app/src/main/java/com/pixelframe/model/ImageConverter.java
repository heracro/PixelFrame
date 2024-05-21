package com.pixelframe.model;

import android.graphics.Bitmap;

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
        int columns = Configuration.MATRIX_WIDTH;
        int rows = Configuration.MATRIX_HEIGHT;
        Bitmap result = Bitmap.createBitmap(columns, rows, Bitmap.Config.ARGB_8888);
        int columnStart = 0;
        int rowStart = 0;
        float exactFragmentWidth = (float)width / columns;
        float exactFragmentHeight = (float)height / rows;
        for (int c = 0; c < columns; ++c) {
            int columnEnd = Math.round(exactFragmentWidth * (c + 1));
            for (int r = 0; r < rows; ++r) {
                int rowEnd = Math.round(exactFragmentHeight * (r + 1));
                Bitmap fragment = Bitmap.createBitmap(image, columnStart, rowStart,
                        columnEnd - columnStart, rowEnd - rowStart);
                int color = algorithm.convert(fragment, columnEnd - columnStart,
                        rowEnd - rowStart);
                result.setPixel(c, r, color);
                rowStart = rowEnd;
            }
            columnStart = columnEnd;
            rowStart = 0;
        }
        return result;
    }
}
