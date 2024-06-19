package com.pixelframe.model.palletes;

import android.graphics.Bitmap;

public abstract class AbstractPalette {

    public Bitmap colorize(Bitmap convertedImage) {
        int columns = convertedImage.getWidth();
        int rows = convertedImage.getHeight();
        Bitmap result = Bitmap.createBitmap(columns, rows, convertedImage.getConfig());
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                result.setPixel(c, r,
                        changePixelColor(convertedImage.getPixel(c, r)));
            }
        }
        return result;
    }

    public abstract int changePixelColor(int pixel);
}
