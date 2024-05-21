package com.pixelframe.eventListeners;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.pixelframe.model.ImageConverter;
import com.pixelframe.model.MatrixLikeResultView;

public class PreviewButtonOnClickListener implements View.OnClickListener{
    private ImageView resultView;
    private ImageConverter imageConverter;
    private Bitmap sourceBitmap;
    private int imageWidth;
    private int imageHeight;
    public PreviewButtonOnClickListener(ImageView resultView, ImageConverter imageConverter,
                                        Bitmap sourceBitmap, int imageWidth, int imageHeight) {
        this.resultView = resultView;
        this.imageConverter = imageConverter;
        this.sourceBitmap = sourceBitmap;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void onClick(View v) {
        Bitmap previewBitmap = MatrixLikeResultView.convert(imageConverter.convert(sourceBitmap, imageWidth, imageHeight));
        resultView.setImageBitmap(previewBitmap);
    }
}
