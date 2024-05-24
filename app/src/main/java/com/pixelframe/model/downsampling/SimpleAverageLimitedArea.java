package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.graphics.Color;

public class SimpleAverageLimitedArea implements SamplingAlgorithm {

    private static final int limit = 3;
    private int maxDist;

    public int convert (Bitmap image, int width, int height, int param1, int param2) {
        maxDist = width / limit;
        int center = width / 2;
        int centralPixel = image.getPixel(width/2, height/2);
        int a = (centralPixel & 0xFF000000) >> 24;
        int r = (centralPixel & 0x00FF0000) >> 16;
        int g = (centralPixel & 0x0000FF00) >> 8;
        int b = centralPixel & 0x000000FF;
        int move_a = 0;
        int move_r = 0;
        int move_g = 0;
        int move_b = 0;
        for (int w = 0; w < width; ++w) {
            for (int h = 0; h < height; ++h) {
                if (weight(distance(center, center, w, h)) > 0) {
                    int pixel = image.getPixel(w, h);
                    move_a -= (Color.alpha(pixel) - a);
                    move_r -= (Color.red(pixel) - r);
                    move_g -= (Color.green(pixel) - g);
                    move_b -= (Color.blue(pixel) - b);
                }
            }
        }
        int pixelCount = (width * width) / (limit * limit);
        return Color.argb(
                a - move_a / pixelCount,
                r - move_r / pixelCount,
                g - move_g / pixelCount,
                b - move_b / pixelCount
        );
    }

    int distance( int wc, int hc, int w, int h) {
        return Math.max(Math.abs(wc - w), Math.abs(hc - h));
    }

    int weight(int distance) {
        if (distance < maxDist) return 1;
        return 0;
    }
}