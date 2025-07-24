package com.deepfake.deepfake_detect.Repository;

import com.deepfake.deepfake_detect.Entity.Upload;
import com.deepfake.deepfake_detect.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UploadRepository extends JpaRepository<Upload, Long> {
    @Query("SELECT u FROM Upload u JOIN FETCH u.model WHERE u.user = :user")
    List<Upload> findByUserWithModel(@Param("user") User user);
}
