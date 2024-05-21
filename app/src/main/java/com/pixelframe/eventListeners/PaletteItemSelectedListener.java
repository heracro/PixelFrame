package com.pixelframe.eventListeners;

import android.view.View;
import android.widget.AdapterView;

import com.pixelframe.model.ImageConverter;

public class PaletteItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final ImageConverter imageConverter;

    public PaletteItemSelectedListener(ImageConverter imageConverter) {
        this.imageConverter = imageConverter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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