package com.pixelframe.model.processing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.pixelframe.model.configuration.Configuration;

public class MatrixLikeResultView {

    private final static int ratio = Configuration.GRID_VIEW_PIXEL_RATIO;
    private final static int imageWidth = Configuration.MATRIX_WIDTH;
    private final static int imageHeight = Configuration.MATRIX_HEIGHT;

    public static Bitmap convert(Bitmap image) {
        if (ratio == 0) return image;
        Log.d("Konwerter", "image.getWidth() = " + image.getWidth());
        Log.d("Konwerter", "image.getHeight() = " + image.getHeight());
        int resultWidth = imageWidth * (ratio + 1) + 1;
        int resultHeight = imageHeight * (ratio + 1) + 1;
        Bitmap result = createBlackBitmap(resultWidth, resultHeight);
        for (int column = 0; column < imageWidth; ++column) {
            for (int row = 0; row < imageHeight; ++row) {
                fillSquare(result, column * (ratio + 1) + 1, row * (ratio + 1) + 1, image.getPixel(column, row));
            }
        }
        return result;
    }

    private static void fillSquare(Bitmap result, int baseCol, int baseRow, int color) {
        for (int c = baseCol; c < baseCol + ratio; ++ c) {
            for (int r = baseRow; r < baseRow + ratio; ++r) {
                result.setPixel(c, r, color);
            }
        }
    }

    private static Bitmap createBlackBitmap(int width, int height) {
        Bitmap blackBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blackBitmap);
        canvas.drawColor(Color.BLACK);
        return blackBitmap;
    }

}