package com.smartclinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ModelConfig {
    
    @Value("${ml.model.directory:ml-models}")
    private String modelDirectory;
    
    @Value("${ml.model.xgboost:xgboost-model-v1.json}")
    private String xgboostModelFile;
    
    @Value("${ml.model.feature-mapping:feature-mapping.json}")
    private String featureMappingFile;
    
    @Value("${ml.training.min-records:100}")
    private int minTrainingRecords;
    
    @Value("${ml.training.auto-retrain:true}")
    private boolean autoRetrain;
    
    @Value("${ml.training.retrain-interval-days:7}")
    private int retrainIntervalDays;
    
    private final ResourceLoader resourceLoader;
    
    public ModelConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Bean
    public Path modelDirectoryPath() throws IOException {
        Path path = Paths.get(modelDirectory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }
    
    @Bean
    public File xgboostModelFile() throws IOException {
        Path modelPath = modelDirectoryPath().resolve(xgboostModelFile);
        if (!Files.exists(modelPath)) {
            // Create default model file if not exists
            Files.createFile(modelPath);
        }
        return modelPath.toFile();
    }
    
    @Bean
    public File featureMappingFile() throws IOException {
        Path mappingPath = modelDirectoryPath().resolve(featureMappingFile);
        if (!Files.exists(mappingPath)) {
            // Create default feature mapping
            String defaultMapping = """
                {
                  "features": [
                    {"name": "age", "type": "numeric", "min": 0, "max": 120},
                    {"name": "gender", "type": "categorical", "values": ["Male", "Female", "Other"]},
                    {"name": "disease", "type": "categorical", "values": ["Fever", "Cold", "Diabetes", "BP", "Migraine", "Pregnancy", "Emergency", "General Checkup", "Follow-up", "Cardiology", "Neurology", "Orthopedic", "Skin", "ENT", "Eye", "Dental", "Physiotherapy"]},
                    {"name": "doctor", "type": "categorical", "values": ["Dr. Arun", "Dr. Ravi", "Dr. Priya", "Dr. Sharma", "Dr. Gupta", "Dr. Singh", "Dr. Reddy", "Dr. Kumar"]},
                    {"name": "dayOfWeek", "type": "categorical", "values": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]},
                    {"name": "timeSlot", "type": "categorical", "values": ["Morning", "Afternoon", "Evening", "Night"]},
                    {"name": "queueLength", "type": "numeric", "min": 0, "max": 100}
                  ],
                  "target": {
                    "name": "actualDuration",
                    "type": "numeric",
                    "min": 1,
                    "max": 120
                  }
                }
                """;
            Files.write(mappingPath, defaultMapping.getBytes());
        }
        return mappingPath.toFile();
    }
    
    @Bean
    public ModelTrainingConfig modelTrainingConfig() {
        return ModelTrainingConfig.builder()
                .minTrainingRecords(minTrainingRecords)
                .autoRetrain(autoRetrain)
                .retrainIntervalDays(retrainIntervalDays)
                .build();
    }
    
    @lombok.Data
    @lombok.Builder
    public static class ModelTrainingConfig {
        private int minTrainingRecords;
        private boolean autoRetrain;
        private int retrainIntervalDays;
    }
}