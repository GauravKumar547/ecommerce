package org.example.userauthservice.services;

import org.example.userauthservice.models.User;

public interface IUserService {
    User getUserById(Long id);
}
