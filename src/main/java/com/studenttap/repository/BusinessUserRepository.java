

package com.studenttap.repository;

import com.studenttap.model.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessUserRepository
        extends JpaRepository<BusinessUser, Long> {

    Optional<BusinessUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<BusinessUser> findByUserType(
        BusinessUser.UserType userType);

    List<BusinessUser> findByUserTypeAndIsActive(
        BusinessUser.UserType userType, Boolean isActive);
}