package com.ecommerce.userauthservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponse(Long userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }
} 