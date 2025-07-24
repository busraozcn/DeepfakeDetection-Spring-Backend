package com.deepfake.deepfake_detect.DTO;

public class FastApiRequest {
    private String file; // Base64 formatında dosya
    private String modelName;

    public FastApiRequest(String file, String modelName) {
        this.file = file;
        this.modelName = modelName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
