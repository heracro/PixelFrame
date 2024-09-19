package com.pixelframe.model.configuration;

import com.pixelframe.model.palletes.AbstractPalette;

public record PaletteDescriptor(Class<? extends AbstractPalette> palette, String name) {
}
