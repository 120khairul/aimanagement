package com.example.academicfeedback.service;

import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.UserAccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    private final UserAccountRepository userAccountRepository;

    public SessionService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public Optional<UserAccount> login(String email, String password) {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .filter(UserAccount::isActive)
                .filter(user -> user.getPassword().equals(password));
    }

    public void storeUser(HttpSession session, UserAccount user) {
        session.setAttribute(CURRENT_USER_ID, user.getId());
    }

    public Optional<UserAccount> currentUser(HttpSession session) {
        Object value = session.getAttribute(CURRENT_USER_ID);
        if (value instanceof Long userId) {
            return userAccountRepository.findById(userId);
        }
        return Optional.empty();
    }

    public boolean hasAnyRole(HttpSession session, Role... roles) {
        Optional<UserAccount> currentUser = currentUser(session);
        if (currentUser.isEmpty()) {
            return false;
        }
        for (Role role : roles) {
            if (currentUser.get().getRole() == role) {
                return true;
            }
        }
        return false;
    }
}
