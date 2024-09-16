package com.example.product_management.controller;

import com.example.product_management.model.Product;
import com.example.product_management.model.User;
import com.example.product_management.service.ProductService;
import com.example.product_management.annotation.AdminOnly;
import com.example.product_management.annotation.MemberOnly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing products and approval flow")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Retrieve all approved products (publicly accessible)
    @GetMapping
    @Operation(summary = "Get all approved products", description = "Retrieve a list of all approved products")
    public List<Product> getAllProducts() {
        return productService.getProductsByRole();
    }

    // Retrieve pending products (admin only)
    @GetMapping("/pending")
    @AdminOnly
    @Operation(summary = "Get pending products", description = "Retrieve a list of products that are pending approval (Admin only)")
    public ResponseEntity<List<Product>> getPendingProducts() {
        return ResponseEntity.ok(productService.getPendingProducts());
    }

    // Add a new product (members only)
    @PostMapping
    @MemberOnly
    @Operation(
        summary = "Add a new product",
        description = "Create a new product. The product will have a 'Pending' status until approved",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @io.swagger.v3.oas.annotations.media.Schema(
                    example = "{ \"name\": \"demoProd1\", \"price\": 10, \"description\": \"general\" }"
                )
            )
        )
    )
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }
        return ResponseEntity.ok(productService.addProduct(product));
    }

    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.addProduct(product));
    }

    // Update a product (members only)
    @PutMapping("/{id}")
    @MemberOnly
    @Operation(
        summary = "Update product",
        description = "Update the product's name, price, or description. The product status will be reset to 'Pending' after any update (Members only)",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @io.swagger.v3.oas.annotations.media.Content(
                schema = @io.swagger.v3.oas.annotations.media.Schema(
                    example = "{ \"name\": \"updatedProdName\", \"price\": 20, \"description\": \"updated description\" }"
                )
            )
        )
    )
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product updatedProduct, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        return ResponseEntity.ok(productService.updateProduct(id, updatedProduct));
    }

    // Approve a product (admin only)
    @PutMapping("/{id}/approve")
    @AdminOnly
    @Operation(summary = "Approve product", description = "Approve a product, changing its status to 'Approved' (Admin only)")
    public ResponseEntity<?> approveProduct(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(productService.approveProduct(id, currentUser));
    }

    // Reject a product (admin only)
    @PutMapping("/{id}/reject")
    @AdminOnly
    @Operation(summary = "Reject product", description = "Reject a product, changing its status to 'Rejected' (Admin only)")
    public ResponseEntity<?> rejectProduct(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(productService.rejectProduct(id, currentUser));
    }

    // Delete a product (members only)
    @DeleteMapping("/{id}")
    @MemberOnly
    @Operation(summary = "Delete product", description = "Delete a product. Only the product owner can delete their own product (Members only)")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("User not logged in");
        }

        try {
            productService.deleteProduct(id); // Ensure only the product owner can delete
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Access denied or product not found");
        }
    }
}

