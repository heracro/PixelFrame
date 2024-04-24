package com.pixelframe.controller.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.pixelframe.controller.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String> mGetContent;
    private PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frameLayout = findViewById(R.id.frame_container);
        photoView = findViewById(R.id.photo_view);
        photoView.setBackgroundColor(Color.WHITE);
        Button buttonLoadImage = findViewById(R.id.button_load);
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Picasso.get().load(uri).into(photoView);
                        photoView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
        );
        frameLayout.post(() -> {
            int width = frameLayout.getWidth();
            int height = frameLayout.getHeight();
            adjustPhotoViewSettingsWithFrameSize(photoView, Math.min(width, height));
        });
        buttonLoadImage.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });
    }

    /**
     * this method should limit zoom out capabilities this way, that picture will always remain
     * squared. So, the smaller dimension of picture should always be not smaller than size of
     * frame. Yet, it doesn't do the job as intended...
     * @param photoView
     * @param frameSize
     */
    private void adjustPhotoViewSettingsWithFrameSize(PhotoView photoView, int frameSize) {
        if (photoView.getDrawable() == null) return;
        float width = photoView.getDrawable().getIntrinsicWidth();
        float height = photoView.getDrawable().getIntrinsicHeight();
        float minScale = Math.min(width, height) / frameSize;
        photoView.setMinimumScale(minScale);
        photoView.setScale(minScale);
    }

}