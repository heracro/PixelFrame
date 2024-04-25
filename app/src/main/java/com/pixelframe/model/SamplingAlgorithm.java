package com.pixelframe.model;

import android.graphics.Bitmap;
import android.graphics.Color;

public interface SamplingAlgorithm {
    Color convert(Bitmap fragment, int width, int height);
}
