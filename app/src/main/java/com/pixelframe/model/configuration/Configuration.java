package com.pixelframe.model.configuration;

import com.pixelframe.model.downsampling.CentralPixel;
import com.pixelframe.model.downsampling.Negative;
import com.pixelframe.model.downsampling.SimpleAverage;
import com.pixelframe.model.downsampling.SimpleAverageLimitedArea;
import com.pixelframe.model.downsampling.WeightedLimitedLinearToDistance;
import com.pixelframe.model.downsampling.WeightedLinearToDistance;
import com.pixelframe.model.downsampling.WeightedSquaredToDistance;
import com.pixelframe.model.palletes.P16Bit565;
import com.pixelframe.model.palletes.P6Bit222;
import com.pixelframe.model.palletes.P7bit232;
import com.pixelframe.model.palletes.P8Bit233;
import com.pixelframe.model.palletes.P8Bit323;
import com.pixelframe.model.palletes.P8Bit332;
import com.pixelframe.model.palletes.P9Bit333;
import com.pixelframe.model.palletes.SourcePalette;

public class Configuration {
    public static final int MATRIX_WIDTH = 64;
    public static final int MATRIX_HEIGHT = 64;
    //Sets how thick pixels are in relation to grid line thickness. Recommended: 2. No grid: 0
    public static final int GRID_VIEW_PIXEL_RATIO = 4;
    public static final String[] PALETTE_SPINNER_CHOICES = new String[] {
        "8-bit",
        "24-bit"
    };

    public final static AlgorithmDescriptor[] ALGORITHMS = new AlgorithmDescriptor[] {
            new AlgorithmDescriptor(CentralPixel.class, "Central pixel color", 0),
            new AlgorithmDescriptor(SimpleAverage.class, "Simple average color", 1),
            new AlgorithmDescriptor(SimpleAverageLimitedArea.class, "Simple avg, limited area (25%)", 1),
            new AlgorithmDescriptor(WeightedLinearToDistance.class, "Weighted linear pythagorean", 2),
            new AlgorithmDescriptor(WeightedLimitedLinearToDistance.class, "Weighted, limited linear pyth.", 2),
            new AlgorithmDescriptor(WeightedSquaredToDistance.class, "Weighted squared pythagorean", 2),
            new AlgorithmDescriptor(Negative.class, "Central Pixel Negative", 0)
    };

    public final static PaletteDescriptor[] PALETTES = new PaletteDescriptor[] {
            new PaletteDescriptor(SourcePalette.class, "24-Bit"),
            new PaletteDescriptor(P6Bit222.class, "6-Bit (2-2-2)"),
            new PaletteDescriptor(P7bit232.class, "7-Bit (2-3-2)"),
            new PaletteDescriptor(P8Bit233.class, "8-Bit (2-3-3)"),
            new PaletteDescriptor(P8Bit332.class, "8-Bit (3-3-2)"),
            new PaletteDescriptor(P8Bit323.class, "8-Bit (3-2-3)"),
            new PaletteDescriptor(P9Bit333.class, "9-Bit (3-3-3)"),
            new PaletteDescriptor(P16Bit565.class, "16-Bit RGB565")
    };

    /*
    FIRST_BLOCK_SIZE define ratio between area above photo view and area below as [value : (1-value)].
    Manipulations may lead to unreadable look.
    VIEW_IMG_WIDTH is a ration of photo view width to screen width. Photo view is square, so it
    also defines its height
     */
    public static final float MAIN_VIEW_FIRST_BLOCK_SIZE = 0.5f;
    public static final float MAIN_VIEW_IMG_WIDTH = 1.0f;
    public static final float CONVERT_VIEW_FIRST_BLOCK_SIZE = 0.1f;
    public static final float CONVERT_VIEW_IMG_WIDTH = 1.0f;
    public static final float TRANSFER_VIEW_FIRST_BLOCK_SIZE = 0.1f;
    public static final float TRANSFER_VIEW_IMG_WIDTH = 0.6f;

    /* depending on memory of target device, number of slots may be higher. We recommend values
    not greater than 8.
     */
    public static final int SLOT_BUTTON_COUNT = 4;

    //at 256576512 we got error "file too big"
    public static final int IMAGE_MAX_SIZE_BYTES = 256576510;
    //above size minus 99% images metadata (<128kB) gives 64111359 pixels.
    public static final int IMAGE_MAX_PIXELS = 64111358;
    public static final int BRIGHTNESS_MIN = 0;
    public static final int BRIGHTNESS_MAX = 100;
    public static final int TIME_MIN = 0;
    public static final int TIME_MAX = 600;
}