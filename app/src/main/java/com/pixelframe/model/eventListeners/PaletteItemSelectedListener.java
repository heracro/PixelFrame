package com.pixelframe.model.eventListeners;

import android.view.View;
import android.widget.AdapterView;

import com.pixelframe.controller.ui.ConvertImageActivity;
import com.pixelframe.model.configuration.Configuration;

public class PaletteItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final ConvertImageActivity activity;

    public PaletteItemSelectedListener(ConvertImageActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.setPaletteClass(Configuration.PALETTES[position].palette);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}