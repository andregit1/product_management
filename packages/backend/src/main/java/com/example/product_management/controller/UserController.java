package com.example.product_management.controller;

import com.example.product_management.dto.AuthRequest;
import com.example.product_management.model.User;
import com.example.product_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users and authentication")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Create a new user", description = "Register a new user",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(name = "Example Request Body", summary = "Default request body example",
                    value = "{\"username\": \"demoUser0\", \"password\": \"123123123\", \"role\": \"MEMBER\"}")
            )
        )
    )
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate a user and create a session",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                schema = @Schema(implementation = AuthRequest.class),
                examples = @ExampleObject(name = "Example Request Body", summary = "Default request body example",
                    value = "{\"username\": \"demoUser0\", \"password\": \"123123123\"}")
            )
        )
    )
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest, HttpSession session) {
        User user = userService.authenticate(authRequest.getUsername(), authRequest.getPassword());
        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate the user's session")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users (Admin only)")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser != null && currentUser.getRole() == User.Role.ADMIN) {
            List<User> users = userService.getAllUsers();
            // Return empty list with 200 OK if no users are found
            return ResponseEntity.ok(users);
        } else {
            // Return 401 Unauthorized if not an admin
            return ResponseEntity.status(401).body("User is not ADMIN");
        }
    }

    @GetMapping("/current")
    @Operation(summary = "Get current user", description = "Retrieve the current logged-in user's details")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User currentUser = userService.getCurrentUser(); // Fetch current user
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser);
        } else {
            return ResponseEntity.status(401).body("User not logged in");
        }
    }
}
