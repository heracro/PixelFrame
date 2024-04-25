package com.pixelframe.model;

import android.view.View;
import android.widget.AdapterView;

import java.lang.reflect.InvocationTargetException;

public class AlgorithmItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private ImageConverter imageConverter;

    public AlgorithmItemSelectedListener(ImageConverter imageConverter) {
        this.imageConverter = imageConverter;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedAlgorithm = (String) parent.getItemAtPosition(position);
        SamplingAlgorithm algorithm = null;
        try {
            algorithm = Configuration.SAMPLING_CLASSES.get(position).getDeclaredConstructor().newInstance();
            imageConverter.setAlgorithm(algorithm);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}