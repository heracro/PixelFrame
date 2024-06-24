package com.pixelframe.model.configuration;

import com.pixelframe.model.downsampling.CentralPixel;
import com.pixelframe.model.downsampling.Negative;
import com.pixelframe.model.downsampling.SimpleAverageCircular;
import com.pixelframe.model.downsampling.SimpleAverageLimitedRectangle;
import com.pixelframe.model.downsampling.SinglePixel;
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
    public static final String PICO_FRAME_NAME = "PicoFram";
    //Sets how thick pixels are in relation to grid line thickness. Recommended: 2. No grid: 0
    public static final int GRID_VIEW_PIXEL_RATIO = 4;

    public final static AlgorithmDescriptor[] ALGORITHMS = new AlgorithmDescriptor[] {
            new AlgorithmDescriptor(CentralPixel.class, "Central pixel color"),
            new AlgorithmDescriptor(SinglePixel.class, "Single pixel from position",
                    "Column", "From which column (as % of width, count from left) pixel should be taken",
                    "Row", "From which row (as % of height, count from top) pixel should be taken"),
            new AlgorithmDescriptor(SimpleAverageCircular.class, "Simple average, circular",
                    "Diameter (%)", "How big, in %, should be diameter of included circle, compared to smaller of fragment sizes"),
            new AlgorithmDescriptor(SimpleAverageLimitedRectangle.class, "Simple average, rect.",
                    "Width (%)", "",
                    "Height (%)", ""),
            new AlgorithmDescriptor(WeightedLinearToDistance.class, "Weighted linear, circ.",
                    "Full weight area", "",
                    "Reduced weight area", ""),
            new AlgorithmDescriptor(WeightedLimitedLinearToDistance.class, "Weighted, limited linear pyth.",
                    "Am i needed at all?", "",
                    "Am I or not??", ""),
            new AlgorithmDescriptor(WeightedSquaredToDistance.class, "Weighted squared pythagorean",
                    "Maximum area (radius)", "",
                    "Rate (slope of weight)", ""),
            new AlgorithmDescriptor(Negative.class, "Central Pixel Negative")
    };

    public final static PaletteDescriptor[] PALETTES = new PaletteDescriptor[] {
            new PaletteDescriptor(SourcePalette.class, "No change"),
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