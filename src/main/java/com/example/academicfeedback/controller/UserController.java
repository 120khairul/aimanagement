package com.example.academicfeedback.controller;

import com.example.academicfeedback.model.Role;
import com.example.academicfeedback.model.UserAccount;
import com.example.academicfeedback.repository.UserAccountRepository;
import com.example.academicfeedback.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserAccountRepository userAccountRepository;
    private final SessionService sessionService;

    public UserController(UserAccountRepository userAccountRepository, SessionService sessionService) {
        this.userAccountRepository = userAccountRepository;
        this.sessionService = sessionService;
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("users", userAccountRepository.findAll());
        return "users/index";
    }

    @GetMapping("/users/new")
    public String newUser(HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("userAccount", new UserAccount());
        model.addAttribute("formAction", "/users");
        return "users/form";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute UserAccount userAccount, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        if (userAccountRepository.existsByEmailIgnoreCase(userAccount.getEmail())) {
            model.addAttribute("userAccount", userAccount);
            model.addAttribute("error", "Email already exists");
            return "users/form";
        }
        userAccountRepository.save(userAccount);
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, HttpSession session, Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("userAccount", userAccountRepository.findById(id).orElseThrow());
        model.addAttribute("formAction", "/users/" + id);
        return "users/form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute UserAccount form,
                             HttpSession session,
                             Model model) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        UserAccount userAccount = userAccountRepository.findById(id).orElseThrow();
        if (userAccountRepository.findByEmailIgnoreCase(form.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .isPresent()) {
            form.setId(id);
            model.addAttribute("userAccount", form);
            model.addAttribute("formAction", "/users/" + id);
            model.addAttribute("error", "Email already exists");
            return "users/form";
        }
        userAccount.setFullName(form.getFullName());
        userAccount.setEmail(form.getEmail());
        userAccount.setPassword(form.getPassword());
        userAccount.setRole(form.getRole());
        userAccount.setDepartment(form.getDepartment());
        userAccount.setActive(form.isActive());
        userAccountRepository.save(userAccount);
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (!sessionService.hasAnyRole(session, Role.ADMIN)) {
            return "redirect:/dashboard";
        }
        UserAccount userAccount = userAccountRepository.findById(id).orElseThrow();
        userAccount.setActive(false);
        userAccountRepository.save(userAccount);
        return "redirect:/users";
    }
}
