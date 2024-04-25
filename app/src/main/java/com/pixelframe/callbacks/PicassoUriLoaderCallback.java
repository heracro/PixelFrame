package com.pixelframe.callbacks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.exifinterface.media.ExifInterface;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;

import java.io.IOException;
import java.io.InputStream;

public class PicassoUriLoaderCallback implements Callback {
    private Context context;
    private PhotoView photoView;
    private TextView instructionText;
    private Uri uri;

    public PicassoUriLoaderCallback(Context context, PhotoView photoView, TextView instructionText, Uri uri) {
        this.context = context;
        this.photoView = photoView;
        this.instructionText = instructionText;
        this.uri = uri;
    }

    @Override
    public void onSuccess() {
        instructionText.setVisibility(View.GONE);
        photoView.post(() -> {
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                ExifInterface exif = new ExifInterface(inputStream);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                inputStream.close();
            } catch (IOException e) {
                Log.d("SourceImage", "Exif data couldn't be parsed");
            }
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotationAngle);
            Bitmap sourceBitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
            Bitmap rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
            photoView.setImageBitmap(rotatedBitmap);

            adjustScale(photoView.getDrawable()); // Twoja metoda dostosowująca skalę
        });
    }
    @Override
    public void onError(Exception e) {
        Log.d("PhotoView","Failed to load picture; onError() called");
    }

    private void adjustScale(Drawable drawable) {
        if (drawable != null) {

            int imageWidth = drawable.getIntrinsicWidth();
            int imageHeight = drawable.getIntrinsicHeight();
            float scale = Math.max(imageWidth, imageHeight) / (float) Math.min(imageWidth, imageHeight);
            float mediumScale = 1.75f * scale;
            float maximumScale = 3.0f * scale;
            photoView.setMinimumScale(scale);
            photoView.setScale(scale);
            photoView.setMediumScale(mediumScale);
            photoView.setMaximumScale(maximumScale);
            Log.d("PhotoView", "Scale set to: " + scale);
            Log.d("PhotoView", "Minimum Scale set to: " + photoView.getMinimumScale());
            Log.d("PhotoView", "Medium Scale set to: " + photoView.getMediumScale());
            Log.d("PhotoView", "Maximum Scale set to: " + photoView.getMaximumScale());
        }
    }
}
