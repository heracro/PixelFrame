package com.pixelframe.model.eventListeners.eventListeners;

import android.view.View;
import android.widget.AdapterView;

import com.pixelframe.controller.ui.ConvertImageActivity;
import com.pixelframe.model.configuration.Configuration;
import com.pixelframe.model.configuration.PaletteDescriptor;
import com.pixelframe.model.downsampling.ImageConverter;
import com.pixelframe.model.palletes.PaletteChanger;
import com.pixelframe.model.palletes.Pallete;

import java.lang.reflect.InvocationTargetException;

public class PaletteItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final PaletteChanger paletteChanger;
    private ConvertImageActivity activity;

    public PaletteItemSelectedListener(PaletteChanger paletteChanger,
                                       ConvertImageActivity activity) {
        this.paletteChanger = paletteChanger;
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        PaletteDescriptor selectedPalette = Configuration.PALETTES[position];
        Pallete palette = null;
        try {
            palette = Configuration.PALETTES[position].palette.getDeclaredConstructor().newInstance();
            paletteChanger.setPalette(palette);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}