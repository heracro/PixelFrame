package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DiffToCentralAverageColor extends AbstractDownsamplingAlgorithm {

    protected int convertFragment(Bitmap image) {
        int move_a = 0, move_r = 0, move_g = 0, move_b = 0, pixelCount = 0;
        int centerPixel = image.getPixel(image.getWidth() / 2, image.getHeight() / 2);
        int a = Color.alpha(centerPixel);
        int r = Color.red(centerPixel);
        int g = Color.green(centerPixel);
        int b = Color.blue(centerPixel);
        for (int w = 0; w < image.getWidth(); ++w) {
            for (int h = 0; h < image.getHeight(); ++h) {
                if (weight(w, h) > 0) {
                    ++pixelCount;
                    int pixel = image.getPixel(w, h);
                    move_a -= (Color.alpha(pixel) - a);
                    move_r -= (Color.red(pixel) - r);
                    move_g -= (Color.green(pixel) - g);
                    move_b -= (Color.blue(pixel) - b);
                }
            }
        }
        return Color.argb(
                a - move_a / pixelCount,
                r - move_r / pixelCount,
                g - move_g / pixelCount,
                b - move_b / pixelCount
        );
    }

    int weight(int column, int row) {
        return 1;
    }

}
