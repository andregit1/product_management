package com.example.product_management.service;

import com.example.product_management.model.User;
import com.example.product_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    // Get all users (for admin usage)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Fetch user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("User not found"));
    }

    // Create new user (registration)
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Authenticate user and store in session
    public User authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            session.setAttribute("user", user.get()); // Store authenticated user in session
            return user.get();
        }
        return null;
    }

    // Logout user
    public void logout() {
        session.invalidate(); // Invalidate session on logout
    }

    // Fetch current session user
    public User getCurrentUser() {
        return (User) session.getAttribute("user");
    }
}
