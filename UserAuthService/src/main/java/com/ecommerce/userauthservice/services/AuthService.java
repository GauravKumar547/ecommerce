package com.ecommerce.userauthservice.services;

import org.antlr.v4.runtime.misc.Pair;
import com.ecommerce.userauthservice.models.User;

public interface AuthService {
    Pair<User,String> login(String email, String password);
    Boolean signup(User user);
    void logout(String token,long userId);
    Boolean validateToken(String token, long userId);
    Pair<Long,String> refreshToken(String token);
}