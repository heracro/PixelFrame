package com.pixelframe.model.processing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.pixelframe.model.configuration.Configuration;
import com.pixelframe.model.eventListeners.BitmapPreviewConsumer;
import com.pixelframe.model.eventListeners.SliderParamUser;

public class LinearBrightnessAdjuster implements SliderParamUser {

    private final Bitmap image;
    private final BitmapPreviewConsumer bitmapConsumer;

    public LinearBrightnessAdjuster(Bitmap image, BitmapPreviewConsumer callback) {
        this.image = image;
        this.bitmapConsumer = callback;
    }

    @Override
    public void transform(int brightness) {
        Bitmap preview = adjustBrightness(image, brightness);
        bitmapConsumer.accept(preview);
    }

    private Bitmap adjustBrightness(Bitmap image, int brightness) {
        int width = Configuration.MATRIX_WIDTH;
        int height = Configuration.MATRIX_HEIGHT;
        Bitmap adjustedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        double b;
        if (brightness <= 50) {
            b = 1.0 * brightness / 50;
        } else {
            b = 9.0 * brightness / 50 - 8;
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getPixel(x, y);
                adjustedBitmap.setPixel(x, y,
                        Color.argb(
                                Color.alpha(pixel),
                                limit((int)(Color.red(pixel) * b)),
                                limit((int)(Color.green(pixel) * b)),
                                limit((int)(Color.blue(pixel) * b))
                        ));
            }
        }
        return adjustedBitmap;
    }

    private int limit(int color) {
        return Math.max(0, Math.min(255, color));
    }

}