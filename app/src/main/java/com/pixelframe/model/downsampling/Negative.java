package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.util.Log;

import com.pixelframe.model.configuration.Configuration;

public class Negative extends AbstractDownsamplingAlgorithm {
    public Negative() {
    }

    public int convertFragment(Bitmap image) {
        int color = image.getPixel(image.getWidth()/2, image.getHeight() /2);
        return (color & 0xFF000000) | (~color & 0x00FF0000) | (~color & 0x0000FF00) | (~color & 0x000000FF);
    }

}