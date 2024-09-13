package com.example.product_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "checked_by", referencedColumnName = "id")
    private User checkedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}