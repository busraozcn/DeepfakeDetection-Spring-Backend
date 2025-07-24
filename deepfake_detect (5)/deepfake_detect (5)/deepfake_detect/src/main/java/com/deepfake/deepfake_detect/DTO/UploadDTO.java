package com.deepfake.deepfake_detect.DTO;

import java.time.LocalDateTime;

public class UploadDTO {
    private Long id;
    private String filename;
    private String filePath;
    private String type;
    private Double aiPercentage;
    private Double realPercentage;
    private String analysisData;
    private String modelName;
    private LocalDateTime uploadTime; // Yeni alan eklendi

    // Constructors
    public UploadDTO() {
    }

    public UploadDTO(Long id, String filename, String filePath, String type, Double aiPercentage, Double realPercentage, String analysisData, String modelName, LocalDateTime uploadTime) {
        this.id = id;
        this.filename = filename;
        this.filePath = filePath;
        this.type = type;
        this.aiPercentage = aiPercentage;
        this.realPercentage = realPercentage;
        this.analysisData = analysisData;
        this.modelName = modelName;
        this.uploadTime = uploadTime; // Yeni alan için constructor güncellemesi
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAiPercentage() {
        return aiPercentage;
    }

    public void setAiPercentage(Double aiPercentage) {
        this.aiPercentage = aiPercentage;
    }

    public Double getRealPercentage() {
        return realPercentage;
    }

    public void setRealPercentage(Double realPercentage) {
        this.realPercentage = realPercentage;
    }

    public String getAnalysisData() {
        return analysisData;
    }

    public void setAnalysisData(String analysisData) {
        this.analysisData = analysisData;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}
