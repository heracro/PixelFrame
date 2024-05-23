package com.pixelframe.controller.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.pixelframe.model.callbacks.PicassoUriLoaderCallback;
import com.pixelframe.controller.R;
import com.pixelframe.model.eventListeners.eventListeners.LayoutDimensionsListener;
import com.pixelframe.model.eventListeners.eventListeners.ConvertButtonOnClickListener;
import com.pixelframe.model.Configuration;
import com.squareup.picasso.Picasso;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> mGetContent;
    private PhotoView photoView;
    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        ViewTreeObserver viewTreeObserver = constraintLayout.getViewTreeObserver();
        LayoutDimensionsListener layoutDimensionsListener = new LayoutDimensionsListener(
                constraintLayout,
                Configuration.MAIN_VIEW_FIRST_BLOCK_SIZE,
                Configuration.MAIN_VIEW_IMG_WIDTH);
        viewTreeObserver.addOnGlobalLayoutListener(layoutDimensionsListener);
        photoView = findViewById(R.id.photo_view);
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageSelection);
        findViewById(R.id.buttonLoad).setOnClickListener(v -> mGetContent.launch("image/*"));
        convertButton = findViewById(R.id.buttonConvert);
        convertButton.setEnabled(false);
        convertButton.setAlpha(0.7f);
        convertButton.setOnClickListener(new ConvertButtonOnClickListener(this));
    }

    private void handleImageSelection(Uri uri) {
        try {
            if (!isValidImage(uri)) return;
        } catch (Exception e) {
            Log.d("PhotoView","Error selecting image: " + e.getMessage());
            return;
        }
        Picasso.get().load(uri).into(photoView, new PicassoUriLoaderCallback(this, photoView, uri));
        convertButton.setEnabled(true);
        convertButton.setAlpha(1f);
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
            if (fileSize > Configuration.IMAGE_MAX_SIZE_BYTES) {
                Log.d("PhotoView", "Chosen file is to big (" + (fileSize / 1024 ) + ")");
                cursor.close();
                return false;
            }
            //file size is ok, let's check picture size (max bitmap size is 256576512 bytes =
            // ~60MPx, so limit of 48MPx seems reasonable.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                inputStream.close();
            }
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;
            long pixelCount = (long) imageWidth * imageHeight;
            if (pixelCount > Configuration.IMAGE_MAX_PIXELS) {
                Log.d("PhotoView", "Chosen file has too many pixels (" + pixelCount + ")");
                cursor.close();
                return false;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return true;
    }

    public PhotoView getPhotoView() {
        return photoView;
    }

}
