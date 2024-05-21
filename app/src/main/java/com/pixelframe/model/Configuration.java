package com.pixelframe.model;

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
    //Sets how thick pixels are in relation to grid line thickness. Recommended: 2
    public static final int GRID_VIEW_PIXEL_RATIO = 2;
    public static final String[] PALETTE_SPINNER_CHOICES = new String[] {
        "8-bit",
        "24-bit"
    };
    public static final String[] ALGORITHM_SPINNER_CHOICES = new String[] {
            "Central pixel color",
            "Simple average color",
            "Simple avg, limited area (25%)",
            "Weighted linear pythagorean",
            "Weighted, limited linear pyth.",
            "Weighted squared pythagorean",
            "Central Pixel Negative"
            //"Weighted gaussian",
            //"Weighted sigmoid, 50% limit",
            //"Weighted sigmoid, 50% limited, 20% unlimited",
            //"Average central 30%",
            //"Heracro's Fantasy 1",
            //"Heracro's Fantasy 2",
            //"Heracro's Fantasy 3",
    };
    public static final List<Class<? extends SamplingAlgorithm>> SAMPLING_CLASSES = Arrays.asList(
            CentralPixel.class,
            SimpleAverage.class,
            SimpleAverageLimitedArea.class,
            WeightedLinearToDistance.class,
            WeightedLimitedLinearToDistance.class,
            WeightedSquaredToDistance.class,
            Negative.class
            //WeightedGaussian.class
            //WeightedSigmoidUpperBound.class
            //WeightedSigmoidTwoBounds.class
            //AverageCentralArea.class
            //Fantasy1.class
            //Fantasy2.class
            //Fantasy3.class
    );

    public static final float MAIN_VIEW_FIRST_BLOCK_SIZE = 0.5f;
    public static final float MAIN_VIEW_IMG_WIDTH = 1.0f;
    public static final float CONVERT_VIEW_FIRST_BLOCK_SIZE = 0.2f;
    public static final float CONVERT_VIEW_IMG_WIDTH = 1.0f;
    public static final float TRANSFER_VIEW_FIRST_BLOCK_SIZE = 0.2f;
    public static final float TRANSFER_VIEW_IMG_WIDTH = 0.6f;

    //at 256576512 we got error "file too big"
    public static final int IMAGE_MAX_SIZE_BYTES = 256576510;
    //above size minus 99% images metadata (<128kB) gives 64111359 pixels.
    public static final int IMAGE_MAX_PIXELS = 64111358;
}
