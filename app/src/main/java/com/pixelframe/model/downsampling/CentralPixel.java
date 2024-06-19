package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.util.Log;

public class CentralPixel extends AbstractDownsamplingAlgorithm {

    int convertFragment(Bitmap image) {
        return image.getPixel(image.getWidth()/2, image.getHeight()/2);
    }

}