package com.ecommerce.productcatalogservice.services.impl;

import com.ecommerce.productcatalogservice.dtos.UserDto;
import com.ecommerce.productcatalogservice.services.AuthenticationService;
import com.ecommerce.commons.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String USER_AUTH_SERVICE_URL = "http://user-auth-service/api/v1/auth/validate";
    private static final String CACHE_PREFIX = "auth_token:";
    private static final long CACHE_DURATION = 30; // 30 minutes

    @Autowired
    public AuthenticationServiceImpl(RestTemplate restTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserDto validateToken(String token) {
        // Check cache first
        String cacheKey = CACHE_PREFIX + token;
        UserDto cachedUser = (UserDto) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        // If not in cache, validate with UserAuthService
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                USER_AUTH_SERVICE_URL,
                HttpMethod.GET,
                requestEntity,
                UserDto.class
            );

            if (response.getBody() != null) {
                // Cache the result
                redisTemplate.opsForValue().set(cacheKey, response.getBody(), CACHE_DURATION, TimeUnit.MINUTES);
                return response.getBody();
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired token");
        }

        throw new UnauthorizedException("Invalid or expired token");
    }

    @Override
    public void invalidateToken(String token) {
        String cacheKey = CACHE_PREFIX + token;
        redisTemplate.delete(cacheKey);
    }
} 