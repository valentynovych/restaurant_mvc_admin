package com.restaurant.restaurant_admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<String> handleError2(HttpServletRequest req, Exception ex) {

        String msg = ex.getMessage();
        if (ex.getCause().getCause() instanceof SQLException) {
            SQLException e = (SQLException) ex.getCause().getCause();

            if (e.getMessage().contains("Duplicate entry ")) {
                msg = e.getMessage().substring(e.getMessage().indexOf("entry "));
            }
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }
}
