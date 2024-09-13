package com.example.product_management.service;

import com.example.product_management.model.Product;
import com.example.product_management.model.User;
import com.example.product_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    // Get products based on user role
    public List<Product> getProductsByRole() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            // Non-logged-in users can only see approved products
            return productRepository.findByStatus(Product.Status.APPROVED);
        } else if (currentUser.getRole() == User.Role.MEMBER) {
            // Members can see all products
            return productRepository.findAll();
        } else if (currentUser.getRole() == User.Role.ADMIN) {
            // Admins can see pending products only
            return productRepository.findByStatus(Product.Status.PENDING);
        }
        throw new RuntimeException("Invalid user role.");
    }

    // Get approved products (for non-logged-in users and members)
    public List<Product> getApprovedProducts() {
        return productRepository.findByStatus(Product.Status.APPROVED);
    }

    // Get pending products (admin only)
    public List<Product> getPendingProducts() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            throw new SecurityException("Only admins can access pending products.");
        }
        return productRepository.findByStatus(Product.Status.PENDING);
    }

    // Add product (members only)
    public Product addProduct(Product product) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
            throw new SecurityException("Only members can add products.");
        }
        product.setStatus(Product.Status.PENDING);  // New products start with 'Pending'
        product.setCreatedBy(currentUser);
        return productRepository.save(product);
    }

    // Update product (members only, and only if they own the product)
    public Product updateProduct(Long productId, Product updatedProduct) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
            throw new SecurityException("Only logged-in members can update products.");
        }

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Ensure the current user is the creator of the product
        if (existingProduct.getCreatedBy() == null || !existingProduct.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only the product owner can update the product.");
        }

        // Update only name, price, and description, and reset the status to 'Pending'
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setStatus(Product.Status.PENDING);  // Reset status to pending after update

        return productRepository.save(existingProduct);
    }

    // Approve product (admin only)
    public Product approveProduct(Long productId, User admin) {
        if (admin == null || admin.getRole() != User.Role.ADMIN) {
            throw new SecurityException("Only admins can approve products.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(Product.Status.APPROVED);
        return productRepository.save(product);
    }

    // Reject product (admin only)
    public Product rejectProduct(Long productId, User admin) {
        if (admin == null || admin.getRole() != User.Role.ADMIN) {
            throw new SecurityException("Only admins can reject products.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(Product.Status.REJECTED);
        return productRepository.save(product);
    }

    // Delete product (members only, and only if they own the product)
    public void deleteProduct(Long productId) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || currentUser.getRole() != User.Role.MEMBER) {
            throw new SecurityException("Only logged-in members can delete products.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Ensure the current user is the creator of the product
        if (product.getCreatedBy() == null || !product.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only the product owner can delete the product.");
        }

        productRepository.deleteById(productId);
    }
}
