package com.ecommerce.userauthservice.services.impl;

import com.ecommerce.userauthservice.models.User;
import com.ecommerce.userauthservice.repos.UserRepository;
import com.ecommerce.userauthservice.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}