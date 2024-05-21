package com.pixelframe.eventListeners;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.pixelframe.controller.ui.ConvertImageActivity;
import com.pixelframe.model.ImageConverter;
import com.pixelframe.model.MatrixLikeResultView;

public class PreviewButtonOnClickListener implements View.OnClickListener{
    ConvertImageActivity activity;
    public PreviewButtonOnClickListener(ConvertImageActivity activity) {
        this.activity = activity;
    }
    @Override
    public void onClick(View v) {
        Bitmap convertedImage = activity.getImageConverter().convert(
                activity.getChosenFragment(),
                activity.getImageWidth(),
                activity.getImageHeight());
        activity.setConvertedFragment(convertedImage);
        Bitmap simulatedImage = MatrixLikeResultView.convert(convertedImage);
        activity.setSimulatedFragmentLook(simulatedImage);
        activity.refreshResultView();
    }
}
