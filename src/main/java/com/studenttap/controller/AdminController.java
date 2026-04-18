
package com.studenttap.controller;

import com.studenttap.dto.ApiResponse;

import com.studenttap.model.*;
import com.studenttap.repository.*;
import com.studenttap.security.JwtUtil;
import com.studenttap.service.CloudinaryService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private AdminRepository adminRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private BusinessUserRepository businessUserRepository;
    @Autowired private AdvertisementRepository advertisementRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private CloudinaryService cloudinaryService;

    @Value("${admin.default.email:admin@codeosoftsolutions.com}")
    private String defaultAdminEmail;

    @Value("${admin.default.password:Admin@123}")
    private String defaultAdminPassword;

    // ===================================================
    // POST /api/admin/login
    // Admin login
    // ===================================================
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String password = req.get("password");

            // Auto-create admin if not exists
            if (!adminRepository.existsByEmail(
                    defaultAdminEmail)) {
                Admin a = new Admin();
                a.setEmail(defaultAdminEmail);
                a.setPassword(passwordEncoder.encode(
                    defaultAdminPassword));
                a.setFullName("Admin");
                adminRepository.save(a);
            }

            Admin admin = adminRepository
                .findByEmail(email)
                .orElse(null);

            if (admin == null ||
                    !passwordEncoder.matches(
                        password, admin.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(
                        "Invalid admin credentials!"));
            }

            admin.setLastLoginAt(LocalDateTime.now());
            adminRepository.save(admin);

            String token = jwtUtil.generateToken(
                "ADMIN:" + email);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("token", token);
            res.put("message", "Admin login successful!");
            res.put("admin", Map.of(
                "email", admin.getEmail(),
                "fullName", admin.getFullName()
            ));

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/admin/dashboard
    // Admin dashboard stats
    // ===================================================
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        try {
            long totalStudents =
                studentRepository.count();
            long totalHostels = businessUserRepository
                .findByUserType(
                    BusinessUser.UserType.HOSTEL).size();
            long totalInstitutes = businessUserRepository
                .findByUserType(
                    BusinessUser.UserType.INSTITUTE).size();
            long totalCompanies = businessUserRepository
                .findByUserType(
                    BusinessUser.UserType.COMPANY).size();
            long totalAds =
                advertisementRepository.count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", totalStudents);
            stats.put("totalHostels", totalHostels);
            stats.put("totalInstitutes", totalInstitutes);
            stats.put("totalCompanies", totalCompanies);
            stats.put("totalAds", totalAds);

            return ResponseEntity.ok(
                ApiResponse.success("Dashboard", stats));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/admin/students
    // Get all students list
    // ===================================================
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students =
                studentRepository.findAll();
            return ResponseEntity.ok(
                ApiResponse.success("Students", students));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/admin/hostels
    // ===================================================
    @GetMapping("/hostels")
    public ResponseEntity<?> getAllHostels() {
        try {
            List<BusinessUser> hostels =
                businessUserRepository.findByUserType(
                    BusinessUser.UserType.HOSTEL);
            return ResponseEntity.ok(
                ApiResponse.success("Hostels", hostels));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/admin/institutes
    // ===================================================
    @GetMapping("/institutes")
    public ResponseEntity<?> getAllInstitutes() {
        try {
            List<BusinessUser> institutes =
                businessUserRepository.findByUserType(
                    BusinessUser.UserType.INSTITUTE);
            return ResponseEntity.ok(
                ApiResponse.success("Institutes",
                    institutes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // GET /api/admin/companies
    // ===================================================
    @GetMapping("/companies")
    public ResponseEntity<?> getAllCompanies() {
        try {
            List<BusinessUser> companies =
                businessUserRepository.findByUserType(
                    BusinessUser.UserType.COMPANY);
            return ResponseEntity.ok(
                ApiResponse.success("Companies",
                    companies));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // DELETE /api/admin/user/{id}?type=STUDENT
    // Block/delete user
    // ===================================================
    @PutMapping("/block-user")
    public ResponseEntity<?> blockUser(
            @RequestBody Map<String, String> req) {
        try {
            String userType = req.get("userType");
            Long userId = Long.parseLong(
                req.get("userId"));
            boolean block = Boolean.parseBoolean(
                req.get("block"));

            if ("STUDENT".equals(userType)) {
                Student s = studentRepository
                    .findById(userId)
                    .orElseThrow();
                s.setIsActive(!block);
                studentRepository.save(s);
            } else {
                BusinessUser bu = businessUserRepository
                    .findById(userId)
                    .orElseThrow();
                bu.setIsActive(!block);
                businessUserRepository.save(bu);
            }

            return ResponseEntity.ok(
                ApiResponse.success(
                    block ? "User blocked!" : "User unblocked!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ===================================================
    // ✅ GET /api/admin/export/students
    // Export students to Excel
    // ===================================================
    @GetMapping("/export/students")
    public ResponseEntity<byte[]> exportStudents()
            throws Exception {

        List<Student> students =
            studentRepository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Header style
        CellStyle headerStyle =
            workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(
            IndexedColors.ROYAL_BLUE.getIndex());
        headerStyle.setFillPattern(
            FillPatternType.SOLID_FOREGROUND);
        Font whiteFont = workbook.createFont();
        whiteFont.setColor(
            IndexedColors.WHITE.getIndex());
        whiteFont.setBold(true);
        headerStyle.setFont(whiteFont);

        // Headers
        String[] headers = {
            "ID", "Full Name", "Username", "Email",
            "Phone", "Skills", "Designation",
            "Type", "City", "Registered Date"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4000);
        }

        // Data rows
        DateTimeFormatter fmt = DateTimeFormatter
            .ofPattern("dd-MM-yyyy");

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(
                s.getFullName() != null ?
                s.getFullName() : "");
            row.createCell(2).setCellValue(
                s.getUsername() != null ?
                s.getUsername() : "");
            row.createCell(3).setCellValue(
                s.getEmail() != null ?
                s.getEmail() : "");
            row.createCell(4).setCellValue(
                s.getPhone() != null ?
                		
                s.getPhone() : "");
           // row.createCell(5).setCellValue(
               // s.getSkills() != null ?
              //  s.getSkills() : "");
            row.createCell(6).setCellValue(
                s.getDesignation() != null ?
                s.getDesignation() : "");
            row.createCell(7).setCellValue(
                s.getIsFresher() != null &&
                s.getIsFresher() ?
                "Fresher" : "Experienced");
            row.createCell(8).setCellValue(
                s.getAddress() != null ?
                s.getAddress() : "");
            row.createCell(9).setCellValue("");
        }

        ByteArrayOutputStream out =
            new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Students.xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument"
                + ".spreadsheetml.sheet"))
            .body(out.toByteArray());
    }

    // ===================================================
    // ✅ GET /api/admin/export/business?type=HOSTEL
    // Export business users to Excel
    // ===================================================
    @GetMapping("/export/business")
    public ResponseEntity<byte[]> exportBusiness(
            @RequestParam String type) throws Exception {

        BusinessUser.UserType userType =
            BusinessUser.UserType.valueOf(
                type.toUpperCase());

        List<BusinessUser> users =
            businessUserRepository.findByUserType(userType);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(type);

        // Header style
        CellStyle headerStyle =
            workbook.createCellStyle();
        Font whiteFont = workbook.createFont();
        whiteFont.setColor(
            IndexedColors.WHITE.getIndex());
        whiteFont.setBold(true);
        headerStyle.setFont(whiteFont);
        headerStyle.setFillForegroundColor(
            IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(
            FillPatternType.SOLID_FOREGROUND);

        String[] headers = {
            "ID", "Full Name", "Business Name",
            "Email", "Phone", "City",
            "Industry", "Status", "Registered"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 4500);
        }

        for (int i = 0; i < users.size(); i++) {
            BusinessUser u = users.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(
                u.getFullName() != null ?
                u.getFullName() : "");
            row.createCell(2).setCellValue(
                u.getBusinessName() != null ?
                u.getBusinessName() : "");
            row.createCell(3).setCellValue(
                u.getEmail() != null ?
                u.getEmail() : "");
            row.createCell(4).setCellValue(
                u.getPhone() != null ?
                u.getPhone() : "");
            row.createCell(5).setCellValue(
                u.getCity() != null ?
                u.getCity() : "");
            row.createCell(6).setCellValue(
                u.getIndustry() != null ?
                u.getIndustry() : "");
            row.createCell(7).setCellValue(
                u.getIsActive() != null &&
                u.getIsActive() ?
                "Active" : "Blocked");
            row.createCell(8).setCellValue(
                u.getCreatedAt() != null ?
                u.getCreatedAt().toString() : "");
        }

        ByteArrayOutputStream out =
            new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename="
                + type + ".xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument"
                + ".spreadsheetml.sheet"))
            .body(out.toByteArray());
    }

    // ===================================================
    // ✅ ADVERTISEMENT APIs
    // ===================================================

    // GET /api/admin/ads - get all ads
    @GetMapping("/ads")
    public ResponseEntity<?> getAllAds() {
        try {
            List<Advertisement> ads =
                advertisementRepository.findAll();
            return ResponseEntity.ok(
                ApiResponse.success("Ads", ads));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/admin/ads - create ad with image
    @PostMapping("/ads")
    public ResponseEntity<?> createAd(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("linkUrl") String linkUrl,
            @RequestParam(value = "targetAudience",
                defaultValue = "ALL")
            String targetAudience,
            @RequestParam(value = "displayOrder",
                defaultValue = "0")
            int displayOrder) {
        try {
            // Upload image to Cloudinary
            String imageUrl = cloudinaryService
                .uploadPhoto(file, "ads");

            Advertisement ad = new Advertisement();
            ad.setTitle(title);
            ad.setImageUrl(imageUrl);
            ad.setLinkUrl(linkUrl);
            ad.setTargetAudience(targetAudience);
            ad.setDisplayOrder(displayOrder);
            ad.setIsActive(true);
            ad.setCreatedAt(LocalDateTime.now());

            advertisementRepository.save(ad);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Ad created! ✅", ad));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                    "Failed: " + e.getMessage()));
        }
    }

    // PUT /api/admin/ads/{id}/toggle - toggle active
    @PutMapping("/ads/{id}/toggle")
    public ResponseEntity<?> toggleAd(
            @PathVariable Long id) {
        try {
            Advertisement ad = advertisementRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Ad not found"));

            ad.setIsActive(!ad.getIsActive());
            advertisementRepository.save(ad);

            return ResponseEntity.ok(
                ApiResponse.success(
                    ad.getIsActive() ?
                    "Ad activated!" : "Ad deactivated!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/admin/ads/{id}
    @DeleteMapping("/ads/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable Long id) {
        try {
            Advertisement ad = advertisementRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Ad not found"));

            // Delete from Cloudinary
            cloudinaryService.deletePhoto(
                cloudinaryService
                    .getPublicIdFromUrl(ad.getImageUrl()));

            advertisementRepository.delete(ad);

            return ResponseEntity.ok(
                ApiResponse.success("Ad deleted! ✅"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}