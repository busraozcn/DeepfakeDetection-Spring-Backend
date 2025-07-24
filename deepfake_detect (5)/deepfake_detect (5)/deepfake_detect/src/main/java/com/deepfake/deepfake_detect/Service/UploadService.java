package com.deepfake.deepfake_detect.Service;

import com.deepfake.deepfake_detect.Entity.Model;
import com.deepfake.deepfake_detect.Entity.Upload;
import com.deepfake.deepfake_detect.Entity.User;
import com.deepfake.deepfake_detect.Repository.ModelRepository;
import com.deepfake.deepfake_detect.Repository.UploadRepository;
import com.deepfake.deepfake_detect.DTO.FastApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UploadService {

    private final UploadRepository uploadRepository;
    private final ModelRepository modelRepository;

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    public UploadService(UploadRepository uploadRepository, ModelRepository modelRepository) {
        this.uploadRepository = uploadRepository;
        this.modelRepository = modelRepository;
    }

    public List<Upload> getUserUploads(User user) {
        try {
            logger.info("Fetching uploads for user: {}", user.getEmail());
            return uploadRepository.findByUserWithModel(user);
        } catch (Exception e) {
            logger.error("Error while fetching uploads for user {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Error fetching uploads for user.");
        }
    }


    public Upload saveUpload(User user, String filename, String filePath, String type, String selectedModel, Double aiPercentage, Double realPercentage, String analysisData) {
        try {
            logger.info("Saving upload for user: {}", user.getEmail());

            Model model = modelRepository.findByName(selectedModel)
                    .orElseThrow(() -> new RuntimeException("Model not found with name: " + selectedModel));

            Upload upload = new Upload();
            upload.setUser(user);
            upload.setFilename(filename);
            upload.setFilePath(filePath);
            upload.setType(type);
            upload.setModel(model);
            upload.setAiPercentage(aiPercentage); // Eklenen alan
            upload.setRealPercentage(realPercentage); // Eklenen alan
            upload.setAnalysisData(analysisData); // Eklenen alan

            Upload savedUpload = uploadRepository.save(upload);
            logger.info("Upload saved successfully: {}", savedUpload.getId());
            return savedUpload;
        } catch (Exception e) {
            logger.error("Error while saving upload for user {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Error saving upload.");
        }
    }


    public Model findModelByName(String modelName) {
        return modelRepository.findByName(modelName)
                .orElseThrow(() -> new RuntimeException("Model not found with name: " + modelName));
    }

    public FastApiResponse sendFileToFastApi(MultipartFile file, String selectedModel, String fastApiUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            body.add("selected_model", selectedModel); // Model adı gönderiliyor

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            logger.info("Sending file to FastAPI with selected model: {}", selectedModel);
            return restTemplate.postForObject(fastApiUrl + "/predict", requestEntity, FastApiResponse.class);
        } catch (Exception e) {
            logger.error("Error while sending file to FastAPI: {}", e.getMessage());
            throw new RuntimeException("Error while sending file to FastAPI.");
        }
    }


}

