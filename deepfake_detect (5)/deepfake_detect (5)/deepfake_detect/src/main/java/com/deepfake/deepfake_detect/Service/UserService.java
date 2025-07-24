package com.deepfake.deepfake_detect.Service;

import com.deepfake.deepfake_detect.Entity.User;
import com.deepfake.deepfake_detect.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Kullanıcı kaydı yapar.
     *
     * @param email Kullanıcının e-posta adresi
     * @param password Kullanıcının şifresi
     * @param firstName Kullanıcının adı
     * @param lastName Kullanıcının soyadı
     * @return Kayıt edilen kullanıcı
     */
    public User registerUser(String email, String password, String firstName, String lastName) {
        // E-posta kontrolü
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use.");
        }

        // Yeni kullanıcı oluşturma
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Şifreyi şifreleme
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setVerificationToken(UUID.randomUUID().toString()); // Doğrulama için token
        user.setTokenExpiry(LocalDateTime.now().plusHours(24)); // Token geçerlilik süresi
        user.setEmailVerified(false); // Doğrulama durumu
        return userRepository.save(user); // Kaydı veritabanına kaydet
    }

    /**
     * Kullanıcı girişini doğrular.
     *
     * @param email Kullanıcının e-posta adresi
     * @param password Kullanıcının şifresi
     * @return Doğrulanmış kullanıcı
     */
    public User authenticateUser(String email, String password) {
        // Kullanıcıyı e-postaya göre bul
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // Şifre kontrolü
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Giriş başarılıysa kullanıcıyı döndür
        return user;
    }

    /**
     * E-posta doğrulama işlemini gerçekleştirir.
     *
     * @param token Kullanıcı doğrulama token'ı
     * @return Doğrulanan kullanıcı
     */
    public Optional<User> verifyEmail(String token) {
        Optional<User> user = userRepository.findByVerificationToken(token);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid verification token.");
        }

        User verifiedUser = user.get();
        if (verifiedUser.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired.");
        }

        // Kullanıcının e-posta doğrulama durumu güncelleniyor
        verifiedUser.setEmailVerified(true);
        verifiedUser.setVerificationToken(null);
        verifiedUser.setTokenExpiry(null);
        userRepository.save(verifiedUser);

        return Optional.of(verifiedUser);
    }

    /**
     * Şifre sıfırlama isteği gönderir.
     *
     * @param email Şifre sıfırlama isteği yapılacak e-posta adresi
     * @return Şifre sıfırlama isteği yapılan kullanıcı
     */
    public Optional<User> requestPasswordReset(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("No user found with this email.");
        }

        // Reset token ve süresi oluşturuluyor
        User resetUser = user.get();
        resetUser.setResetToken(UUID.randomUUID().toString());
        resetUser.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(resetUser);

        return Optional.of(resetUser);
    }

    /**
     * Şifreyi sıfırlar.
     *
     * @param token Şifre sıfırlama token'ı
     * @param newPassword Yeni şifre
     * @return Şifre sıfırlama işlemi yapılan kullanıcı
     */
    public Optional<User> resetPassword(String token, String newPassword) {
        Optional<User> user = userRepository.findByResetToken(token);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid reset token.");
        }

        User resetUser = user.get();
        if (resetUser.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token has expired.");
        }

        // Yeni şifre ayarlanıyor
        resetUser.setPassword(passwordEncoder.encode(newPassword));
        resetUser.setResetToken(null);
        resetUser.setResetTokenExpiry(null);
        userRepository.save(resetUser);

        return Optional.of(resetUser);
    }
}
