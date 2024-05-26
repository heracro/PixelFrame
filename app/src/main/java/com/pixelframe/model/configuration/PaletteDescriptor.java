package com.pixelframe.model.configuration;

import com.pixelframe.model.downsampling.SamplingAlgorithm;
import com.pixelframe.model.palletes.Pallete;

public class PaletteDescriptor {
    public Class<? extends Pallete> palette;
    public String name;
    public PaletteDescriptor(Class<? extends Pallete> palette,
                             String name) {
        this.palette = palette;
        this.name = name;
    }
}
