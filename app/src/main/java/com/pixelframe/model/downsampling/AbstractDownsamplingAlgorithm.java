package com.pixelframe.model.downsampling;

import android.graphics.Bitmap;
import android.util.Log;

import com.pixelframe.model.configuration.Configuration;

public abstract class AbstractDownsamplingAlgorithm {
    protected Integer param1;
    protected Integer param2;

    public void setParam1(Integer param1) {
        this.param1 = param1;
    }

    public void setParam2(Integer param2) {
        this.param2 = param2;
    }

    public Bitmap convert(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int columns = Configuration.MATRIX_WIDTH;
        int rows = Configuration.MATRIX_HEIGHT;
        Bitmap result = Bitmap.createBitmap(columns, rows, Bitmap.Config.ARGB_8888);
        int columnStart = 0;
        int rowStart = 0;
        float exactFragmentWidth = (float)width / columns;
        Log.d("Abstract Downsampling Algorithm","exactFragmentWidth = " + exactFragmentWidth);
        float exactFragmentHeight = (float)height / rows;
        Log.d("Abstract Downsampling Algorithm","exactFragmentHeight = " + exactFragmentHeight);
        for (int c = 0; c < columns; ++c) {
            int columnEnd = Math.round(exactFragmentWidth * (c + 1));
            Log.d("Abstract Downsampling Algorithm","columnEnd = " + columnEnd);
            if (columnEnd >= width) {
                Log.d("Abstract Downsampling Algorithm", "convert(): columnEnd = " + columnEnd + "; width = "+ width);
                columnEnd = width -1;
            }
            for (int r = 0; r < rows; ++r) {
                int rowEnd = Math.round(exactFragmentHeight * (r + 1));
                Log.d("Abstract Downsampling Algorithm","rowEnd = " + rowEnd);
                if (rowEnd >= height) {
                    Log.d("Abstract Downsampling Algorithm", "convert(): rowEnd = " + rowEnd + "; height = "+ height);
                    rowEnd = height -1;
                }
                Log.d("Abstract Downsampling Algorithm", "Trying to call: Bitmap.createBitmap(image, "
                    + columnStart + ", " + rowStart + ", " + (columnEnd - columnStart) + ", " + (rowEnd - rowStart) + ");");
                Bitmap fragment = Bitmap.createBitmap(image, columnStart, rowStart,
                        columnEnd - columnStart, rowEnd - rowStart);
                Log.d("Abstract Downsampling Algorithm", "Bitmap created. Attempting to convert fragment into pixel");
                int color = convertFragment(fragment);
                result.setPixel(c, r, color);
                rowStart = rowEnd;
            }
            columnStart = columnEnd;
            rowStart = 0;
        }
        return result;
    }

    abstract int convertFragment(Bitmap fragment);
}
