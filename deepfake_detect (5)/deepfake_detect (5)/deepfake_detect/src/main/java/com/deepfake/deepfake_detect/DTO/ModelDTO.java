package com.deepfake.deepfake_detect.DTO;

public class ModelDTO {
    private Long id;
    private String name;
    private String description;
    private String version;

    // Constructors
    public ModelDTO() {
    }

    public ModelDTO(Long id, String name, String description, String version) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.version = version;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
