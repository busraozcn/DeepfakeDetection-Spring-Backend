package com.deepfake.deepfake_detect.Controller;

import com.deepfake.deepfake_detect.DTO.FastApiResponse;
import com.deepfake.deepfake_detect.DTO.UploadDTO;
import com.deepfake.deepfake_detect.Entity.Upload;
import com.deepfake.deepfake_detect.Entity.User;
import com.deepfake.deepfake_detect.Repository.ModelRepository;
import com.deepfake.deepfake_detect.Repository.UserRepository;
import com.deepfake.deepfake_detect.Service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private final UploadService uploadService;
    private final UserRepository userRepository;
    private final ModelRepository modelRepository;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public UploadController(UploadService uploadService, UserRepository userRepository, ModelRepository modelRepository) {
        this.uploadService = uploadService;
        this.userRepository = userRepository;
        this.modelRepository = modelRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<List<UploadDTO>> getUserUploads(Authentication authentication) {
        String userEmail = authentication.getName();
        logger.info("Fetching uploads for user: {}", userEmail);

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UploadDTO> uploads = uploadService.getUserUploads(authenticatedUser).stream().map(upload -> {
            UploadDTO uploadDTO = new UploadDTO();
            uploadDTO.setId(upload.getId());
            uploadDTO.setFilename(upload.getFilename());
            uploadDTO.setFilePath(upload.getFilePath());
            uploadDTO.setType(upload.getType());
            uploadDTO.setAiPercentage(upload.getAiPercentage());
            uploadDTO.setRealPercentage(upload.getRealPercentage());
            uploadDTO.setAnalysisData(upload.getAnalysisData());
            uploadDTO.setModelName(upload.getModel() != null ? upload.getModel().getName() : "Unknown Model");
            return uploadDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(uploads);
    }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndAnalyzeFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "selected_model", required = true) String selectedModel,
            Authentication authentication) {

        String userEmail = authentication.getName();
        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            FastApiResponse response = uploadService.sendFileToFastApi(file, selectedModel, fastApiUrl);

            if (response == null) {
                throw new RuntimeException("No response from FastAPI");
            }

            // FastAPI'den gelen sonuçları alıyoruz
            double confidence = response.getConfidence();
            String label = response.getLabel();

            Upload upload = null;
            if ("vjdevane-deepfake detection".equals(selectedModel)) {
                // Eğer model "vjdevane-deepfake detection" ise özel işlem yap
                upload = uploadService.saveUpload(
                        authenticatedUser,
                        file.getOriginalFilename(),
                        "path_not_saved",
                        file.getContentType(),
                        selectedModel,
                        100 - confidence,   // Real yüzdesi, confidence'ten türetiliyor
                        confidence,         // Fake yüzdesi
                        label               // Analiz sonucu
                );
            } else {
                // Diğer modeller için standart işlem
                upload = uploadService.saveUpload(
                        authenticatedUser,
                        file.getOriginalFilename(),
                        "path_not_saved",
                        file.getContentType(),
                        selectedModel,
                        confidence,         // AI yüzdesi
                        100 - confidence,   // Gerçek yüzdesi
                        label               // Analiz sonucu
                );
            }

            // Yanıt oluşturuluyor
            UploadDTO uploadDTO = new UploadDTO();
            uploadDTO.setId(upload.getId());
            uploadDTO.setFilename(upload.getFilename());
            uploadDTO.setFilePath(upload.getFilePath());
            uploadDTO.setType(upload.getType());
            uploadDTO.setAiPercentage(confidence);
            uploadDTO.setRealPercentage(100 - confidence);
            uploadDTO.setAnalysisData(label);
            uploadDTO.setModelName(selectedModel);

            return ResponseEntity.ok(uploadDTO);
        } catch (Exception e) {
            logger.error("Error processing upload: {}", e.getMessage());
            return ResponseEntity.status(500).body("An error occurred while processing the upload.");
        }
    }




}
