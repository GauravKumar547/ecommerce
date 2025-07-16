package com.ecommerce.orderservice.dtos;

import lombok.Data;

@Data
public class ShippingAddressResponse {
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phone;
    private String email;
    private String specialInstructions;
} 