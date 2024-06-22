package com.pixelframe.model.eventListeners;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.pixelframe.controller.ui.activity.ConvertImageActivity;
import com.pixelframe.model.processing.MatrixLikeResultView;
import com.pixelframe.model.downsampling.AbstractDownsamplingAlgorithm;
import com.pixelframe.model.palletes.AbstractPalette;

import java.util.Objects;

public class PreviewButtonOnClickListener implements View.OnClickListener{
    final ConvertImageActivity activity;

    public PreviewButtonOnClickListener(ConvertImageActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        AbstractDownsamplingAlgorithm downsampler;
        try {
            downsampler = activity.getDownsamplerClass().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Log.d("PreviewButtonOnClickListener", Objects.requireNonNull(e.getMessage()));
            return;
        }
        downsampler.setParam1(activity.getParam1Value());
        downsampler.setParam2(activity.getParam2Value());
        Log.d("PreviewButtonOnClickListener", "Param1: " + activity.getParam1Value() +
                "; Param2: " + activity.getParam2Value() + "; ImgWidth: " + activity.getImageWidth() +
                "; ImgHeight: " + activity.getImageHeight());
        Bitmap convertedImage = downsampler.convert(
                activity.getChosenFragment());
        AbstractPalette paletteChanger = null;
        try {
            paletteChanger = activity.getPaletteClass().newInstance();

        } catch (IllegalAccessException | InstantiationException e) {
            Log.d("PreviewButtonOnClickListener", Objects.requireNonNull(e.getMessage()));
        }
        if (paletteChanger != null) {
            convertedImage = paletteChanger.colorize(convertedImage);
        }
        activity.setConvertedFragment(convertedImage);
        Bitmap simulatedImage = MatrixLikeResultView.convert(convertedImage);
        activity.setSimulatedFragmentLook(simulatedImage);
        activity.refreshResultView();
        activity.enableSendButton(true);
    }
}
