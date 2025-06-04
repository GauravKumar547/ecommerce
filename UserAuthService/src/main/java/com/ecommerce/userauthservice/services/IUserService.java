package com.ecommerce.userauthservice.services;

import com.ecommerce.userauthservice.models.User;

public interface IUserService {
    User getUserById(Long id);
}