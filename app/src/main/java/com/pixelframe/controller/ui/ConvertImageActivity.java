package com.pixelframe.controller.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.pixelframe.controller.R;

public class ConvertImageActivity extends AppCompatActivity {
    private ImageView sourceView;
    private Bitmap sourceBitmap;
    private ImageView resultView;
    private Bitmap resultBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        String imagePath = getIntent().getStringExtra("image_path");
        int frameWidth = getIntent().getIntExtra("frame_width", 0);
        int frameHeight = getIntent().getIntExtra("frame_height", 0);
        sourceBitmap = BitmapFactory.decodeFile(imagePath);
        sourceView = findViewById(R.id.source_image);
        ViewGroup.LayoutParams layoutParams = sourceView.getLayoutParams();
        layoutParams.width = frameWidth;
        layoutParams.height = frameHeight;
        sourceView.setLayoutParams(layoutParams);
        Bitmap scaledBitmap = loadScaledBitmap(imagePath, frameWidth, frameHeight);
        sourceView.setImageBitmap(scaledBitmap);
        resultView = findViewById(R.id.result_image);
    }

    public static Bitmap loadScaledBitmap(String imagePath, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
