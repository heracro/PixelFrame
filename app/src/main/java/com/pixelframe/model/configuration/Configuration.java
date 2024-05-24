package com.pixelframe.model.configuration;

import com.pixelframe.model.SamplingAlgorithm;
import com.pixelframe.model.downsampling.CentralPixel;
import com.pixelframe.model.downsampling.Negative;
import com.pixelframe.model.downsampling.SimpleAverage;
import com.pixelframe.model.downsampling.SimpleAverageLimitedArea;
import com.pixelframe.model.downsampling.WeightedLimitedLinearToDistance;
import com.pixelframe.model.downsampling.WeightedLinearToDistance;
import com.pixelframe.model.downsampling.WeightedSquaredToDistance;

import java.util.Arrays;
import java.util.List;

public class Configuration {
    public static final int MATRIX_WIDTH = 64;
    public static final int MATRIX_HEIGHT = 64;
    //Sets how thick pixels are in relation to grid line thickness. Recommended: 2. No grid: 0
    public static final int GRID_VIEW_PIXEL_RATIO = 3;
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
//    public static final String[] ALGORITHM_SPINNER_CHOICES = new String[] {
//            "Central pixel color",
//            "Simple average color",
//            "Simple avg, limited area (25%)",
//            "Weighted linear pythagorean",
//            "Weighted, limited linear pyth.",
//            "Weighted squared pythagorean",
//            "Central Pixel Negative"
//    };
//    public static final List<Class<? extends SamplingAlgorithm>> SAMPLING_CLASSES = Arrays.asList(
//            CentralPixel.class,
//            SimpleAverage.class,
//            SimpleAverageLimitedArea.class,
//            WeightedLinearToDistance.class,
//            WeightedLimitedLinearToDistance.class,
//            WeightedSquaredToDistance.class,
//            Negative.class
//    );

    public static final float MAIN_VIEW_FIRST_BLOCK_SIZE = 0.5f;
    public static final float MAIN_VIEW_IMG_WIDTH = 1.0f;
    public static final float CONVERT_VIEW_FIRST_BLOCK_SIZE = 0.1f;
    public static final float CONVERT_VIEW_IMG_WIDTH = 1.0f;
    public static final float TRANSFER_VIEW_FIRST_BLOCK_SIZE = 0.1f;
    public static final float TRANSFER_VIEW_IMG_WIDTH = 0.6f;
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
