package com.pixelframe.model.eventListeners.eventListeners;

import android.view.View;
import android.widget.AdapterView;

import com.pixelframe.controller.ui.ConvertImageActivity;
import com.pixelframe.model.configuration.AlgorithmDescriptor;
import com.pixelframe.model.configuration.Configuration;
import com.pixelframe.model.downsampling.ImageConverter;
import com.pixelframe.model.SamplingAlgorithm;

import java.lang.reflect.InvocationTargetException;

public class AlgorithmItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final ImageConverter imageConverter;
    private final ConvertImageActivity activity;

    public AlgorithmItemSelectedListener(ImageConverter imageConverter,
                                         ConvertImageActivity activity) {
        this.imageConverter = imageConverter;
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AlgorithmDescriptor selectedAlgorithmDescriptor = Configuration.ALGORITHMS[position];
        SamplingAlgorithm algorithm = null;
        try {
            algorithm = Configuration.ALGORITHMS[position].algorithm.getDeclaredConstructor().newInstance();
            imageConverter.setAlgorithm(algorithm);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        activity.setParameterControlsEnabled(selectedAlgorithmDescriptor.parameters);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        activity.setParameterControlsEnabled(0);
    }
}