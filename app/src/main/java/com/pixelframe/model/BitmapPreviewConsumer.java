package com.pixelframe.model;

import android.graphics.Bitmap;

@FunctionalInterface
public interface BitmapPreviewConsumer {
    void accept(Bitmap bitmap);
}
