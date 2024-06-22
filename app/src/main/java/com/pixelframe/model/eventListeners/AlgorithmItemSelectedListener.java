package com.pixelframe.model.eventListeners;

import android.view.View;
import android.widget.AdapterView;
import com.pixelframe.controller.ui.activity.ConvertImageActivity;
import com.pixelframe.model.configuration.Configuration;

public class AlgorithmItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final ConvertImageActivity activity;

    public AlgorithmItemSelectedListener(ConvertImageActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.setAlgorithmPosition(position);
        activity.setDownsamplerClass(Configuration.ALGORITHMS[position].algorithm);
        activity.updateParameterHints(position);
        activity.initSliders();
        activity.setParameterControlsEnabled(Configuration.ALGORITHMS[position].parameters);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        activity.setParameterControlsEnabled(0);
    }
}