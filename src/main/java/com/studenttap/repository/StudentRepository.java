
package com.studenttap.repository;

import com.studenttap.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student by email (used during login)
    Optional<Student> findByEmail(String email);

    // Find student by username (used for public portfolio page)
    Optional<Student> findByUsername(String username);

    // Check if email already exists (used during registration)
    boolean existsByEmail(String email);

    // Check if username already exists (used during registration)
    boolean existsByUsername(String username);
}