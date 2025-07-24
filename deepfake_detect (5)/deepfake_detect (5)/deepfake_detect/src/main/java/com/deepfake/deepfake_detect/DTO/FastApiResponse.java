package com.deepfake.deepfake_detect.DTO;

public class FastApiResponse {
    private Double confidence;
    private String label;

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
