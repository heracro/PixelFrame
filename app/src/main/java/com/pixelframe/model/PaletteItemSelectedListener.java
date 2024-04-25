package com.pixelframe.model;

import android.view.View;
import android.widget.AdapterView;

public class PaletteItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private ImageConverter imageConverter;

    public PaletteItemSelectedListener(ImageConverter imageConverter) {
        this.imageConverter = imageConverter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedPalette = (String) parent.getItemAtPosition(position);
        if (position == 0) {
            imageConverter.setPalette(ImageConverter.PALETTE_8BIT);
        } else {
            imageConverter.setPalette(ImageConverter.PALETTE_24BIT);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}