package com.pixelframe.model.palletes;

import android.graphics.Bitmap;

import com.pixelframe.model.configuration.Configuration;

public class PaletteChanger {

    private Pallete pallete;

    public PaletteChanger() {
    }

    public void setPalette(Pallete pallete) {
        this.pallete = pallete;
    }

    public Bitmap colorize(Bitmap convertedImage) {
        int columns = Configuration.MATRIX_WIDTH;
        int rows = Configuration.MATRIX_HEIGHT;
        Bitmap result = Bitmap.createBitmap(columns, rows, Bitmap.Config.ARGB_8888);
        for (int c = 0; c < columns; ++c) {
            for (int r = 0; r < rows; ++r) {
                result.setPixel(c, r,
                        pallete.colorize(convertedImage.getPixel(c, r)));
            }
        }
        return result;
    }
}
