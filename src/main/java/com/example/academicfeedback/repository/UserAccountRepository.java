package com.example.academicfeedback.repository;

import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    List<UserAccount> findByRole(Role role);

    boolean existsByEmailIgnoreCase(String email);
}
