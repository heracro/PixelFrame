package com.pixelframe.controller.ui;

import android.os.Bundle;
import android.widget.Button;
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

        photoView = findViewById(R.id.photo_view);
        Button buttonLoadImage = findViewById(R.id.button_load);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Picasso.get().load(uri).into(photoView);
                    }
                }
        );

        buttonLoadImage.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            PhotoView photoView = findViewById(R.id.photo_view);
//            Picasso.get().load(imageUri).into(photoView);
//        }
//    }
}