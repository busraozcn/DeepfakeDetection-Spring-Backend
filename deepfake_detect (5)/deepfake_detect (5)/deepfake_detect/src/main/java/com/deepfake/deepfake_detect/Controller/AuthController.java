package com.deepfake.deepfake_detect.Controller;

import com.deepfake.deepfake_detect.Entity.User;
import com.deepfake.deepfake_detect.DTO.UserDTO;
import com.deepfake.deepfake_detect.Service.UserService;
import com.deepfake.deepfake_detect.Service.JWTTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User registeredUser = userService.registerUser(user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(registeredUser.getId());
        userDTO.setEmail(registeredUser.getEmail());
        userDTO.setFirstName(registeredUser.getFirstName());
        userDTO.setLastName(registeredUser.getLastName());
        userDTO.setEmailVerified(registeredUser.isEmailVerified());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userService.authenticateUser(email, password);
        String token = jwtTokenProvider.generateToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return userService.verifyEmail(token)
                .map(u -> ResponseEntity.ok("Email verified successfully."))
                .orElse(ResponseEntity.badRequest().body("Invalid or expired token."));
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        userService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return userService.resetPassword(token, newPassword)
                .map(u -> ResponseEntity.ok("Password reset successfully."))
                .orElse(ResponseEntity.badRequest().body("Invalid or expired token."));
    }
}
