package com.pixelframe.model.eventListeners.eventListeners;

import android.graphics.Bitmap;
import android.view.View;

import com.pixelframe.controller.ui.ConvertImageActivity;
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
        convertedImage = activity.getPaletteChanger().colorize(convertedImage);
        activity.setConvertedFragment(convertedImage);
        Bitmap simulatedImage = MatrixLikeResultView.convert(convertedImage);
        activity.setSimulatedFragmentLook(simulatedImage);
        activity.refreshResultView();
        activity.enableSendButton(true);
    }
}
