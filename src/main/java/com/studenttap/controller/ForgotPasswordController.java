
package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.Student;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ForgotPasswordController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // Store OTPs temporarily in memory
    // Key: email, Value: {otp, expiry}
    private final Map<String, Map<String, Object>>
        otpStore = new HashMap<>();

    // ===================================================
    // STEP 1 — Student enters email
    // POST /api/auth/forgot-password
    // Sends OTP to email
    // ===================================================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Email is required!"));
            }

            // Check if student exists
            Student student = studentRepository
                .findByEmail(email)
                .orElse(null);

            if (student == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "No account found with this email!"));
            }

            // Generate 6 digit OTP
            String otp = String.format("%06d",
                new Random().nextInt(999999));

            // Store OTP with 10 minute expiry
            Map<String, Object> otpData = new HashMap<>();
            otpData.put("otp", otp);
            otpData.put("expiry",
                System.currentTimeMillis() + 600000);
            otpStore.put(email, otpData);

            // Send OTP email
            sendOtpEmail(email,
                student.getFullName(), otp);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "OTP sent to " + maskEmail(email)
                    + "! Valid for 10 minutes."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Failed to send OTP: "
                    + e.getMessage()));
        }
    }

    // ===================================================
    // STEP 2 — Student verifies OTP
    // POST /api/auth/verify-otp
    // ===================================================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");

            if (email == null || otp == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Email and OTP required!"));
            }

            // Check OTP exists
            Map<String, Object> otpData =
                otpStore.get(email);
            if (otpData == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "OTP expired! Request new OTP."));
            }

            // Check expiry
            long expiry = (Long) otpData.get("expiry");
            if (System.currentTimeMillis() > expiry) {
                otpStore.remove(email);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "OTP expired! Request new OTP."));
            }

            // Check OTP matches
            String storedOtp = (String) otpData.get("otp");
            if (!storedOtp.equals(otp)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Invalid OTP! Try again."));
            }

            // OTP verified! Mark as verified
            otpData.put("verified", true);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "OTP verified! ✅ Set new password."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // STEP 3 — Student sets new password
    // POST /api/auth/reset-password
    // ===================================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("password");
            String confirmPassword =
                request.get("confirmPassword");

            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Email and password required!"));
            }

            // Check passwords match
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Passwords do not match!"));
            }

            // Check password length
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Password must be at least"
                        + " 6 characters!"));
            }

            // Check OTP was verified
            Map<String, Object> otpData =
                otpStore.get(email);
            if (otpData == null ||
                    !Boolean.TRUE.equals(
                        otpData.get("verified"))) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Please verify OTP first!"));
            }

            // Update password
            Student student = studentRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Student not found"));

            student.setPassword(
                passwordEncoder.encode(newPassword));
            studentRepository.save(student);

            // Remove OTP from store
            otpStore.remove(email);

            // Send confirmation email
            sendPasswordChangedEmail(email,
                student.getFullName());

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Password reset successful! ✅"
                    + " Please login with new password."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // Helper — Send OTP email
    // ===================================================
    private void sendOtpEmail(String email,
            String name, String otp) {
        try {
            SimpleMailMessage message =
                new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(
                "StudentTap — Password Reset OTP");
            message.setText(
                "Dear " + name + ",\n\n"
                + "Your OTP for password reset is:\n\n"
                + "🔐 " + otp + "\n\n"
                + "This OTP is valid for 10 minutes.\n\n"
                + "If you did not request this, "
                + "please ignore this email.\n\n"
                + "Best regards,\n"
                + "StudentTap Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email send failed: "
                + e.getMessage());
            throw new RuntimeException(
                "Failed to send email. "
                + "Please try again.");
        }
    }

    // ===================================================
    // Helper — Send password changed confirmation
    // ===================================================
    private void sendPasswordChangedEmail(
            String email, String name) {
        try {
            SimpleMailMessage message =
                new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(
                "StudentTap — Password Changed");
            message.setText(
                "Dear " + name + ",\n\n"
                + "Your password has been successfully"
                + " reset.\n\n"
                + "If you did not do this, please"
                + " contact us immediately.\n\n"
                + "Best regards,\n"
                + "StudentTap Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(
                "Confirmation email failed: "
                + e.getMessage());
        }
    }

    // ===================================================
    // Helper — Mask email for security
    // example: s***@gmail.com
    // ===================================================
    private String maskEmail(String email) {
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) return email;
        return name.charAt(0)
            + "***@" + domain;
    }
}