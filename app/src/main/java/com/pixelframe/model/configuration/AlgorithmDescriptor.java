package com.pixelframe.model.configuration;

import com.pixelframe.model.downsampling.AbstractDownsamplingAlgorithm;

public class AlgorithmDescriptor {
    public final Class<? extends AbstractDownsamplingAlgorithm> algorithm;
    public final String name;
    public int parameters;
    public final String parameterName1;
    public String parameterHint1;
    public final String parameterName2;
    public String parameterHint2;

    public AlgorithmDescriptor(Class<? extends AbstractDownsamplingAlgorithm> algorithm,
                               String name,
                               String parameterName1, String parameterHint1,
                               String parameterName2, String parameterHint2) {
        this.algorithm = algorithm;
        this.name = name;
        this.parameters = 2;
        this.parameterName1 = parameterName1;
        this.parameterHint1 = parameterHint1;
        this.parameterName2 = parameterName2;
        this.parameterHint2 = parameterHint2;
    }

    public AlgorithmDescriptor(Class<? extends AbstractDownsamplingAlgorithm> algorithm,
                               String name, String parameterName1, String parameterHint1) {
        this(algorithm, name, parameterName1, parameterHint1, "", "");
        this.parameters = 1;
    }

    public AlgorithmDescriptor(Class<? extends AbstractDownsamplingAlgorithm> algorithm,
                               String name) {
        this(algorithm, name, "", "");
        this.parameters = 0;
    }

}