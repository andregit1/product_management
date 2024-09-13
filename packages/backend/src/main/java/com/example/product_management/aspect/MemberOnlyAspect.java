package com.example.product_management.aspect;

import com.example.product_management.model.User;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.ResponseEntity;

@Aspect
@Component
public class MemberOnlyAspect {

    @Autowired
    private HttpSession session;

    @Before("@annotation(com.example.product_management.annotation.MemberOnly)")
    public Object checkMemberAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        User currentUser = (User) session.getAttribute("user");

        // Check if the user is logged in and has the ADMIN role
        if (currentUser != null && currentUser.getRole() == User.Role.MEMBER) {
            return joinPoint.proceed();  // Proceed with the method execution
        } else if (currentUser == null) {
            // Return a 404 Not Found response if the user is not logged in
            return ResponseEntity.status(404).body("Unauthorized.");
        } else {
            return ResponseEntity.status(403).body("Access denied. Only members are allowed to perform this action.");
        }
    }
}
