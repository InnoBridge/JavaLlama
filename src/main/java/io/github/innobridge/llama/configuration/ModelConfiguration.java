package io.github.innobridge.llama.configuration;

import de.kherud.llama.ModelParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfiguration {

    @Bean
    public ModelParameters modelParameters(
            @Value("${model.file.path}") String modelFilePath,
            @Value("${model.gpu.layers}") int nGpuLayers) {
        System.out.println("modelFilePath: " + modelFilePath);
        System.out.println("nGpuLayers: " + nGpuLayers);
        return new ModelParameters()
                .setModelFilePath(modelFilePath)
                .setNGpuLayers(nGpuLayers);
    }
}
