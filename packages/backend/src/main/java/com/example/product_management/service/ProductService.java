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

    // Get all approved products
    public List<Product> getApprovedProducts() {
        // return productRepository.findByStatus(Product.Status.APPROVED);
        return productRepository.findAll();
    }

    // Get pending products
    public List<Product> getPendingProducts() {
        // Assume we check if the user is an admin, but this has been removed for simplicity
        return productRepository.findByStatus(Product.Status.PENDING);
    }

    // Add product (assumes the user is a member)
    public Product addProduct(Product product) {
        product.setStatus(Product.Status.PENDING);  // New products start with 'Pending'
        // Assume user is a member and is automatically set as creator
        User currentUser = userService.getCurrentUser();
        product.setCreatedBy(currentUser);
        return productRepository.save(product);
    }

    // Update product (assumes the user is a member and is the owner)
    public Product updateProduct(Long productId, Product updatedProduct) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update only name, price, and description, and reset the status to 'Pending'
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setStatus(Product.Status.PENDING);  // Reset status to pending after update

        return productRepository.save(existingProduct);
    }

    // Approve product
    public Product approveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(Product.Status.APPROVED);
        return productRepository.save(product);
    }

    // Reject product
    public Product rejectProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(Product.Status.REJECTED);
        return productRepository.save(product);
    }

    // Delete product (assumes the user is a member and is the owner)
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.deleteById(productId);
    }
}
