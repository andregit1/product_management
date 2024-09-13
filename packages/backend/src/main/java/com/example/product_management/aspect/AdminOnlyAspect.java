package com.example.product_management.aspect;

import com.example.product_management.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Aspect
@Component
public class AdminOnlyAspect {

    @Around("@annotation(com.example.product_management.annotation.AdminOnly)")
    public Object checkAdminAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the current session
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User currentUser = (User) session.getAttribute("user");

        // Check if the user is logged in and has the ADMIN role
        if (currentUser != null && currentUser.getRole() == User.Role.ADMIN) {
            return joinPoint.proceed();  // Proceed with the method execution
        } else if (currentUser == null) {
            // Return a 404 Not Found response if the user is not logged in
            return ResponseEntity.status(404).body("Unauthorized.");
        } else {
            // Return a 403 Forbidden response if the user is not an admin
            return ResponseEntity.status(403).body("Access denied. Admin only.");
        }
    }
}
