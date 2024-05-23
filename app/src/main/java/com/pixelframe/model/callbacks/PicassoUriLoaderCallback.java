package com.pixelframe.model.callbacks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import java.io.IOException;
import java.io.InputStream;

public class PicassoUriLoaderCallback implements Callback {
    private final Context context;
    private final PhotoView photoView;
    private final Uri uri;

    public PicassoUriLoaderCallback(Context context, PhotoView photoView, Uri uri) {
        this.context = context;
        this.photoView = photoView;
        this.uri = uri;
    }

    @Override
    public void onSuccess() {
        photoView.post(() -> {
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    ExifInterface exif = new ExifInterface(inputStream);
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.d("SourceImage", "Exif data couldn't be parsed");
            }
            //if there's no meta, assume orientation is normal.
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotationAngle);
            Bitmap sourceBitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
            Bitmap rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
            photoView.setImageBitmap(rotatedBitmap);

            adjustScale(photoView.getDrawable());
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
            if (scale == photoView.getMinimumScale()) {
                return;
            }
            float mediumScale = 1.75f * scale;
            float maximumScale = 3.0f * scale;
            if (photoView.getMinimumScale() < scale) {
                photoView.setMaximumScale(maximumScale);
                photoView.setMediumScale(mediumScale);
                photoView.setMinimumScale(scale);
                photoView.setScale(scale);
            } else {
                photoView.setMinimumScale(scale);
                photoView.setMediumScale(mediumScale);
                photoView.setMaximumScale(maximumScale);
                photoView.setScale(scale);
            }
        }
    }
}
