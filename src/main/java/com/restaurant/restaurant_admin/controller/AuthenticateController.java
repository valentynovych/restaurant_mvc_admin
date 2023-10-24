package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.config.CustomAuthenticationSuccessHandler;
import com.restaurant.restaurant_admin.model.StaffLoginRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthenticateController {

    private final AuthenticationManager authManager;
    private final CustomAuthenticationSuccessHandler successHandler;


    @GetMapping("/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("admin/login");
    }

    @PostMapping("/signin")
    public @ResponseBody ResponseEntity<?> login(@Valid @ModelAttribute StaffLoginRequest loginRequest,
                                                 BindingResult result,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
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
            successHandler.onAuthenticationSuccess(request, response, authentication);

        } catch (BadCredentialsException e) {
            result.addError(new FieldError("loginRequest", "password", "Не правильний логін або пароль"));
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
