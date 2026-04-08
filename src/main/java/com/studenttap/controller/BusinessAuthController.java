

package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;
import com.studenttap.model.BusinessUser;
import com.studenttap.repository.BusinessUserRepository;
import com.studenttap.repository.StudentRepository;
import com.studenttap.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class BusinessAuthController {

    @Autowired
    private BusinessUserRepository businessUserRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${admin.email:admin@codeosoftsolutions.com}")
    private String adminEmail;

    @Value("${admin.whatsapp:9999999999}")
    private String adminWhatsapp;

    // Blocked email domains for companies
    private static final List<String> BLOCKED_DOMAINS =
        Arrays.asList(
            "gmail.com", "yahoo.com", "hotmail.com",
            "outlook.com", "rediffmail.com", "ymail.com",
            "live.com", "aol.com", "icloud.com",
            "protonmail.com", "mail.com"
        );

    // ===================================================
    // POST /api/auth/register-business
    // Register Hostel / Institute / Company
    // ===================================================
    @PostMapping("/register-business")
    public ResponseEntity<?> registerBusiness(
            @RequestBody Map<String, String> request) {
        try {
            String userTypeStr = request.get("userType");
            String fullName = request.get("fullName");
            String businessName = request.get("businessName");
            String email = request.get("email");
            String password = request.get("password");
            String phone = request.get("phone");
            String city = request.get("city");
            String industry = request.get("industry");

            // Validate required fields
            if (userTypeStr == null || fullName == null ||
                    email == null || password == null ||
                    businessName == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "All required fields must be filled!"));
            }

            // Parse user type
            BusinessUser.UserType userType;
            try {
                userType = BusinessUser.UserType
                    .valueOf(userTypeStr.toUpperCase());
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Invalid user type!"));
            }

            // ✅ Company email validation
            if (userType == BusinessUser.UserType.COMPANY) {
                String domain = email.toLowerCase()
                    .split("@").length > 1
                    ? email.toLowerCase().split("@")[1]
                    : "";
                if (BLOCKED_DOMAINS.contains(domain)) {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error(
                            "Companies must use official email! "
                            + "Gmail/Yahoo/Hotmail not allowed."));
                }
            }

            // Check email already exists
            if (businessUserRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Email already registered! "
                        + "Please login."));
            }

            // Also check student emails
            if (studentRepository.findByEmail(email)
                    .isPresent()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Email already registered "
                        + "as student!"));
            }

            // Password length
            if (password.length() < 6) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Password must be at least "
                        + "6 characters!"));
            }

            // Create business user
            BusinessUser user = new BusinessUser();
            user.setUserType(userType);
            user.setFullName(fullName);
            user.setBusinessName(businessName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(phone);
            user.setCity(city);
            user.setIndustry(industry);
            user.setIsActive(true);
            user.setIsApproved(true);
            user.setCreatedAt(LocalDateTime.now());

            businessUserRepository.save(user);

            return ResponseEntity.ok(
                ApiResponse.success(
                    userType.name() + " registered successfully! "
                    + "Please login."));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Registration failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // POST /api/auth/login-business
    // Login for Hostel / Institute / Company
    // ===================================================
    @PostMapping("/login-business")
    public ResponseEntity<?> loginBusiness(
            @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String userTypeStr = request.get("userType");

            if (email == null || password == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Email and password required!"));
            }

            // Find user
            BusinessUser user = businessUserRepository
                .findByEmail(email)
                .orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "No account found with this email!"));
            }

            // Check password
            if (!passwordEncoder.matches(
                    password, user.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Invalid password!"));
            }

            // Check if active
            if (!user.getIsActive()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Account is blocked. "
                        + "Contact admin."));
            }

            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            businessUserRepository.save(user);

            // ✅ Company login alert to admin
            if (user.getUserType() ==
                    BusinessUser.UserType.COMPANY) {
                sendCompanyLoginAlert(user);
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(email);

            // Build response
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("fullName", user.getFullName());
            userData.put("businessName", user.getBusinessName());
            userData.put("email", user.getEmail());
            userData.put("userType", user.getUserType().name());
            userData.put("phone", user.getPhone());
            userData.put("city", user.getCity());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("user", userData);
            response.put("message", "Login successful!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Login failed: " + e.getMessage()));
        }
    }

    // ===================================================
    // ✅ Company Login Alert to Admin
    // Sends Email + WhatsApp link
    // ===================================================
    private void sendCompanyLoginAlert(BusinessUser company) {
        try {
            String time = LocalDateTime.now()
                .format(DateTimeFormatter
                    .ofPattern("dd MMM yyyy, hh:mm a"));

            String alertMsg =
                "🏢 COMPANY LOGIN ALERT!\n\n" +
                "Company: " + company.getBusinessName() + "\n" +
                "Contact: " + company.getFullName() + "\n" +
                "Email: " + company.getEmail() + "\n" +
                "Industry: " + company.getIndustry() + "\n" +
                "Phone: " + company.getPhone() + "\n" +
                "Time: " + time + "\n\n" +
                "This company is viewing student data on StudentTap.";

            // Send email to admin
            if (mailSender != null) {
                try {
                    SimpleMailMessage mail =
                        new SimpleMailMessage();
                    mail.setTo(adminEmail);
                    mail.setSubject(
                        "🏢 Company Login Alert: "
                        + company.getBusinessName());
                    mail.setText(alertMsg);
                    mailSender.send(mail);
                } catch (Exception e) {
                    System.out.println(
                        "Alert email failed: " + e.getMessage());
                }
            }

            // Log WhatsApp alert link
            String waMsg = alertMsg.replace("\n", "%0A")
                .replace(" ", "%20");
            String waLink = "https://wa.me/"
                + adminWhatsapp + "?text=" + waMsg;
            System.out.println(
                "✅ Company login alert!");
            System.out.println(
                "Company: " + company.getBusinessName());
            System.out.println(
                "WhatsApp Alert: " + waLink);

        } catch (Exception e) {
            System.out.println(
                "Alert failed: " + e.getMessage());
        }
    }
}