package com.pixelframe.model.configuration;

import com.pixelframe.model.SamplingAlgorithm;

public class AlgorithmDescriptor {
    public Class<? extends SamplingAlgorithm> algorithm;
    public String name;
    public int parameters;
    public String parameterName1;
    public String parameterName2;

    public AlgorithmDescriptor(Class<? extends SamplingAlgorithm> algorithm,
                               String name, int parameters, String parameterName1,
                               String parameterName2) {
        this.algorithm = algorithm;
        this.name = name;
        this.parameters = parameters;
        this.parameterName1 = parameterName1;
        this.parameterName2 = parameterName2;
    }

    public AlgorithmDescriptor(Class<? extends SamplingAlgorithm> algorithm,
                               String name, int parameters, String parameterName1) {
        this(algorithm, name, parameters, parameterName1, "");
    }

    public AlgorithmDescriptor(Class<? extends SamplingAlgorithm> algorithm,
                               String name, int parameters) {
        this(algorithm, name, parameters, "");
    }

}
