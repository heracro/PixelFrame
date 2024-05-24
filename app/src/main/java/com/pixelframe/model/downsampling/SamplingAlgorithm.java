package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;

public interface SamplingAlgorithm {
    int convert(Bitmap fragment, int width, int height, int param1, int param2);
}