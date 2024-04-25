package com.pixelframe.controller.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.pixelframe.controller.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> mGetContent;
    private PhotoView photoView;
    private TextView instructionText;
    private Button convertButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoView = findViewById(R.id.photo_view);
        instructionText = findViewById(R.id.instruction_text);
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageSelection);
        Button buttonLoadImage = findViewById(R.id.button_load);
        buttonLoadImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        convertButton = findViewById(R.id.button_convert);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get source picture and viewing frame size (it's square)
                int frameSize = getResources().getDisplayMetrics().widthPixels;
                Log.d("CropImage", "frameSize = " + frameSize);
                Bitmap fullPicture = ((BitmapDrawable)photoView.getDrawable()).getBitmap();
                //Get visible rectangle from original picture and matrix of transformations
                RectF visibleRect = photoView.getDisplayRect();
                Matrix imageMatrix = new Matrix();
                photoView.getDisplayMatrix(imageMatrix);
                float[] matrixValues = new float[9];
                imageMatrix.getValues(matrixValues);
                Log.d("CropImage", "Matrix: " + Arrays.toString(matrixValues));
                //get translation and scale values
                float scaleX = matrixValues[Matrix.MSCALE_X];
                Log.d("CropImage", "scaleX = " + scaleX);
                float scaleY = matrixValues[Matrix.MSCALE_Y];
                Log.d("CropImage", "scaleY = " + scaleY);
                float translateX = matrixValues[Matrix.MTRANS_X];
                Log.d("CropImage", "translateX = " + translateX);
                float translateY = matrixValues[Matrix.MTRANS_Y];
                Log.d("CropImage", "translateY = " + translateY);
                float invScaleX = 1 / scaleX;
                Log.d("CropImage", "invScaleX = " + invScaleX);
                float invScaleY = 1 / scaleY;
                Log.d("CropImage", "invScaleY = " + invScaleY);
                // Calculate the actual position of the image's visible rectangle from the display rect
                RectF displayRect = photoView.getDisplayRect();
                int actualLeft = (int) ((-translateX) * invScaleX);
                Log.d("CropImage", "actualLeft = " + actualLeft);
                int actualTop = (int) ((-translateY) * invScaleY);
                Log.d("CropImage", "actualTop = " + actualTop);
                int actualRight = (int) ((frameSize - translateX) * invScaleX);
                Log.d("CropImage", "actualRight = " + actualRight);
                int actualBottom = (int) ((frameSize - translateY) * invScaleY);
                Log.d("CropImage", "actualBottom = " + actualBottom);
                // Calculate width and height of the cropped area
                int width = actualRight - actualLeft;
                Log.d("CropImage", "width = " + width);
                int height = actualBottom - actualTop;
                Log.d("CropImage", "height = " + height);
                //crop visible fragment
                String filePath = null;
                if (fullPicture != null && actualLeft >= 0 && actualTop >= 0 && width > 0 && height > 0) {
                    Bitmap croppedPicture = Bitmap.createBitmap(fullPicture, actualLeft, actualTop, width, height);
                    filePath = saveBitmapToCache(croppedPicture);
                }
                if (filePath != null) {
                    Intent intent = new Intent(MainActivity.this, ConvertImageActivity.class);
                    intent.putExtra("imagePath", filePath);
                    intent.putExtra("width", width);
                    intent.putExtra("height", height);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Error saving image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
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

    private void handleImageSelection(Uri uri) {
        try {
            if (!isValidImage(uri)) {
                return;
            }

            Picasso.get().load(uri).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    instructionText.setVisibility(View.GONE);
                    photoView.post(() -> {
                        Drawable drawable = photoView.getDrawable();
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
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.d("PhotoView","Failed to load picture; onError() called");
                }
            });
        } catch (Exception e) {
            Log.d("PhotoView","Error selecting image: " + e.getMessage());
        }
    }

    /**
     * Let's make sure app won't crash if huge image is loaded (tested 64MPx). For now. Later on
     * this could change to allow let's say scaling them down up to some reasonable size. Any way
     * this method must do the job without loading image itself - for example by checking meta data
     * @param uri location of chosen file
     * @return true when file is within size limits (pixels, bytes...), fail if opening could crash
     * the app.
     * @throws Exception when closing file fails.
     */
    private boolean isValidImage(Uri uri) throws Exception {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            //so we know we have a file, let's check file size.
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            long fileSize = cursor.getLong(sizeIndex);
            if (fileSize > 10 * 1024 * 1024) {
                instructionText.setText("File is too large to process.");
                Log.d("PhotoView", "Chosen file is to big (>10MB)");
                cursor.close();
                return false;
            }
            //file size is ok, let's check picture size (max bitmap size is 256576512 bytes =
            // ~60MPx, so limit of 48MPx seems reasonable.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;
            long pixelCount = (long) imageWidth * imageHeight;
            if (pixelCount > 48000000) {
                instructionText.setText("Image is too large to process.");
                Log.d("PhotoView", "Chosen file has too many pixels (>48M)");
                cursor.close();
                return false;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return true;
    }
}
