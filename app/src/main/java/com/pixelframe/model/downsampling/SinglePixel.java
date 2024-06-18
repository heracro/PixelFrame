package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;

public class SinglePixel extends AbstractDownsamplingAlgorithm {
    public int convertFragment(Bitmap fragment) {
        int col = Math.min(fragment.getWidth() * param1 / 100, fragment.getWidth() - 1);
        int row = Math.min(fragment.getHeight() * param2 / 100, fragment.getHeight() -1);
        return fragment.getPixel(col, row);
    }


}
