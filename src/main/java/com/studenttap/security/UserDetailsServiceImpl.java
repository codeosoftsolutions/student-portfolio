package com.studenttap.security;

import com.studenttap.model.Student;
import com.studenttap.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Student student = studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "Student not found with email: " + email)
            );

        // ✅ Fixed for Spring Security 7
        return User.builder()
            .username(student.getEmail())
            .password(student.getPassword())
            .authorities(Collections.emptyList())
            .build();
    }
}