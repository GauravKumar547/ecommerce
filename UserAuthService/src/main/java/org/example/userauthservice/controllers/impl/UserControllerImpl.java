package org.example.userauthservice.controllers.impl;

import org.example.userauthservice.controllers.UserController;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.mappers.UserMapper;
import org.example.userauthservice.models.User;
import org.example.userauthservice.services.IUserService;
import org.example.userauthservice.utils.ApiResponse;
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

