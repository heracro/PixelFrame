package com.pixelframe.model;

import android.graphics.Bitmap;
import android.graphics.Color;

public class MatrixLikeResultView {
    public Bitmap convert(Bitmap image) {
        return image;
    }
    public Bitmap convertTo1of9(Bitmap image) {
        int imageWidth = Configuration.MATRIX_WIDTH;
        int imageHeight = Configuration.MATRIX_HEIGHT;
        int newWidth = imageWidth * 2 + 1;
        int newHeight = imageHeight * 2 + 1;
        Bitmap result = Bitmap.createBitmap(
                imageWidth * 2 + 1,
                imageHeight * 2 + 1,
                Bitmap.Config.ARGB_8888
        );
        for (int w = 0; w < newWidth; ++w) {
            for (int h = 0; h < newHeight; ++h) {
                if (w % 2 == 1 && h % 2 == 1) {
                    result.setPixel(w, h,
                            image.getPixel((w-1)/2, (h-1)/2));
                } else {
                    result.setPixel(w, h, Color.BLACK);
                }
            }
        }
        return result;
    }
    private Bitmap convertMoreLikeCircularPoints(Bitmap image) {
        return image;
    }
    private Bitmap FourPixelsSingleDivision(Bitmap image) {
        int imageWidth = Configuration.MATRIX_WIDTH;
        int imageHeight = Configuration.MATRIX_HEIGHT;
        int newWidth = imageWidth * 2 + 1;
        int newHeight = imageHeight * 2 + 1;
        return image;
    }
}
/*
 public int getPixel (int x, int y)
Added in API level 1

Returns the Color at the specified location. Throws an exception if x or y are out of bounds (negative or >= to the width or height respectively). The returned color is a non-premultiplied ARGB value.
Parameters
x 	The x coordinate (0...width-1) of the pixel to return
y 	The y coordinate (0...height-1) of the pixel to return
Returns

    The argb Color at the specified coordinate

Throws
IllegalArgumentException 	if x, y exceed the bitmap's bounds

 public void setPixel (int x, int y, int color)
Added in API level 1

Write the specified Color into the bitmap (assuming it is mutable) at the x,y coordinate. The color must be a non-premultiplied ARGB value.

Parameters
x 	The x coordinate of the pixel to replace (0...width-1)
y 	The y coordinate of the pixel to replace (0...height-1)
color 	The ARGB color to write into the bitmap
Throws
IllegalStateException 	if the bitmap is not mutable
IllegalArgumentException 	if x, y are outside of the bitmap's bounds.
 */