package com.ecommerce.userauthservice.controllers.impl;

import com.ecommerce.commons.utils.response.ApiResponse;
import com.ecommerce.userauthservice.controllers.UserController;
import com.ecommerce.userauthservice.dtos.UserDto;
import com.ecommerce.userauthservice.mappers.UserMapper;
import com.ecommerce.userauthservice.models.User;
import com.ecommerce.userauthservice.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserControllerImpl implements UserController {
    private final IUserService userService;

    @Autowired
    public UserControllerImpl(IUserService userService) {
        this.userService = userService;
    }
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserDetails(@PathVariable Long id) {
        ApiResponse<UserDto> apiResponse = new ApiResponse<>();
        if (id == null) {
           throw new IllegalArgumentException("User id not provided");
        }
        User user = userService.getUserById(id);
        if (user == null) {
            apiResponse.setError("User not found").setStatus(HttpStatus.NOT_FOUND);
            return ApiResponse.getResponseEntity(apiResponse);
        }
        apiResponse.setData(UserMapper.toUserDto(user));
        return ApiResponse.getResponseEntity(apiResponse);
    }
}