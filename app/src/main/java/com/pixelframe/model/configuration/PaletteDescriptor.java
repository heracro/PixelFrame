package com.pixelframe.model.configuration;

import com.pixelframe.model.palletes.AbstractPalette;

public class PaletteDescriptor {
    public final Class<? extends AbstractPalette> palette;
    public final String name;
    public PaletteDescriptor(Class<? extends AbstractPalette> palette,
                             String name) {
        this.palette = palette;
        this.name = name;
    }
}
