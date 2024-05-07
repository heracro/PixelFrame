package com.pixelframe.controller.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.pixelframe.callbacks.PicassoUriLoaderCallback;
import com.pixelframe.controller.R;
import com.pixelframe.eventListeners.ConvertButtonOnClickListener;
import com.squareup.picasso.Picasso;
import java.io.InputStream;

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
        convertButton.setOnClickListener(new ConvertButtonOnClickListener(this, photoView));
    }

    private void handleImageSelection(Uri uri) {
        try {
            if (!isValidImage(uri)) return;
        } catch (Exception e) {
            Log.d("PhotoView","Error selecting image: " + e.getMessage());
            return;
        }
        Picasso.get().load(uri).into(photoView,
                new PicassoUriLoaderCallback(this, photoView, instructionText, uri));
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
