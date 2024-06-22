package com.pixelframe.model.eventListeners;

import android.graphics.Bitmap;

@FunctionalInterface
public interface BitmapPreviewConsumer {
    void accept(Bitmap bitmap);
}
