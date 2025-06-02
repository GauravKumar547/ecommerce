package org.ecommerce.orderservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ShippingAddress {
    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String phone;

    private String email;

    private String specialInstructions;
} 