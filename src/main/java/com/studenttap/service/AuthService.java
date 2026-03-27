


package com.studenttap.service;

import com.studenttap.dto.LoginRequest;
import com.studenttap.dto.LoginResponse;
import com.studenttap.dto.RegisterRequest;
import com.studenttap.model.Student;
import com.studenttap.repository.StudentRepository;
import com.studenttap.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ===================================================
    // REGISTER - Create new student account
    // ===================================================
    public String register(RegisterRequest request) {

        // Check if email already used
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered! Please login.");
        }

        // Check if username already taken
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken! Please choose another.");
        }

        // Create new student
        Student student = new Student();
        student.setFullName(request.getFullName());
        student.setUsername(request.getUsername().toLowerCase().trim());
        student.setEmail(request.getEmail().toLowerCase().trim());

        // Encrypt password before saving
        student.setPassword(passwordEncoder.encode(request.getPassword()));

        student.setPhone(request.getPhone());
        student.setIsFresher(request.getIsFresher());
        student.setIsActive(true);

        studentRepository.save(student);

        return "Registration successful! Welcome " + request.getFullName();
    }

    // ===================================================
    // LOGIN - Verify student and return JWT token
    // ===================================================
    public LoginResponse login(LoginRequest request) {

        // Find student by email
        Student student = studentRepository
            .findByEmail(request.getEmail().toLowerCase().trim())
            .orElseThrow(() ->
                new RuntimeException("No account found with this email!")
            );

        // Check if account is active
        if (!student.getIsActive()) {
            throw new RuntimeException("Your account is deactivated. Contact support.");
        }

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new RuntimeException("Wrong password! Please try again.");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(student.getEmail());

        // Return token + student info
        return new LoginResponse(
            token,
            student.getUsername(),
            student.getFullName(),
            student.getEmail(),
            student.getIsFresher()
        );
    }
}