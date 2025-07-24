package com.deepfake.deepfake_detect.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "uploads")
public class Upload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 255)
    private String filePath;

    @Column(nullable = false, length = 10)
    private String type; // "photo" or "video"

    @Column(nullable = false)
    private LocalDateTime uploadTime = LocalDateTime.now();

    @Column
    private Double aiPercentage;

    @Column
    private Double realPercentage;

    @Column
    private String analysisData; // JSON format

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
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

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Upload(Long id, User user, String filename, String filePath, String type, LocalDateTime uploadTime, Double aiPercentage, Double realPercentage, String analysisData, Model model) {
        this.id = id;
        this.user = user;
        this.filename = filename;
        this.filePath = filePath;
        this.type = type;
        this.uploadTime = uploadTime;
        this.aiPercentage = aiPercentage;
        this.realPercentage = realPercentage;
        this.analysisData = analysisData;
        this.model = model;
    }

    public Upload() {
    }
}