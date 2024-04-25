package com.pixelframe.eventListeners;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.github.chrisbanes.photoview.PhotoView;
import com.pixelframe.controller.ui.ConvertImageActivity;
import java.util.Arrays;

public class ConvertButtonOnClickListener implements View.OnClickListener {
    private Context context;
    private PhotoView photoView;
    public ConvertButtonOnClickListener(Context context, PhotoView photoView) {
        this.context = context;
        this.photoView = photoView;
    }
    @Override
    public void onClick(View v) {
        //Get source picture and viewing frame size (it's square)
        int frameSize = context.getResources().getDisplayMetrics().widthPixels;
        Bitmap fullPicture = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
        //Get visible rectangle from original picture and matrix of transformations
        RectF visibleRect = photoView.getDisplayRect();
        Matrix imageMatrix = new Matrix();
        photoView.getDisplayMatrix(imageMatrix);
        float[] matrixValues = new float[9];
        imageMatrix.getValues(matrixValues);
        //get translation and scale values
        float scaleX = matrixValues[Matrix.MSCALE_X];
        float scaleY = matrixValues[Matrix.MSCALE_Y];
        float translateX = matrixValues[Matrix.MTRANS_X];
        float translateY = matrixValues[Matrix.MTRANS_Y];
        float invScaleX = 1 / scaleX;
        float invScaleY = 1 / scaleY;
        // Calculate the actual position of the image's visible rectangle from the display rect
        RectF displayRect = photoView.getDisplayRect();
        int actualLeft = (int) ((-translateX) * invScaleX);
        int actualTop = (int) ((-translateY) * invScaleY);
        int actualRight = (int) ((frameSize - translateX) * invScaleX);
        int actualBottom = (int) ((frameSize - translateY) * invScaleY);
        // Calculate width and height of the cropped area
        int width = actualRight - actualLeft;
        int height = actualBottom - actualTop;
        //crop visible fragment
        String filePath = null;
        if (fullPicture != null && actualLeft >= 0 && actualTop >= 0 && width > 0 && height > 0) {
            Bitmap croppedPicture = Bitmap.createBitmap(fullPicture, actualLeft, actualTop, width, height);
            filePath = saveBitmapToCache(croppedPicture);
        }
        if (filePath != null) {
            Intent intent = new Intent(context, ConvertImageActivity.class);
            intent.putExtra("imagePath", filePath);
            intent.putExtra("width", width);
            intent.putExtra("height", height);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
    private String saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            return cachePath + "/image.png";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
