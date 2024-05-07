package com.pixelframe.model;

import android.graphics.Bitmap;
import android.graphics.Color;

public interface SamplingAlgorithm {
    int convert(Bitmap fragment, int width, int height);
}
