package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.model.staff.StaffLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthenticateController {

    private final AuthenticationManager authManager;

    @GetMapping("/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("admin/login");
    }

    @PostMapping("/signin")
    public void login(@Valid @ModelAttribute StaffLoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication;
        try {
            authentication = authManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                    loginRequest.getPassword()));
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);
            System.out.println("==========================================================");
            System.out.println("Login Success");
            System.out.println("==========================================================");
        } catch (BadCredentialsException e) {
            System.err.println("==========================================================");
            System.err.println("Login Error");
            System.err.println("==========================================================");
        }
    }
}
