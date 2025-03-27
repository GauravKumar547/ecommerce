package org.example.userauthservice.services.impl;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.exceptions.NoActiveSessionFoundException;
import org.example.userauthservice.models.Session;
import org.example.userauthservice.models.State;
import org.example.userauthservice.models.User;
import org.example.userauthservice.repos.SessionRepository;
import org.example.userauthservice.repos.UserRepository;
import org.example.userauthservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;
    private final SecretKey secretKey;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, SessionRepository sessionRepository,
                           SecretKey secretKey) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
        this.secretKey = secretKey;

    }
    @Override
    public Pair<User, String> login(String email, String password) {
        return null;
    }

    @Override
    public Boolean signup(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            return false;
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public void logout(String token, long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUserId(token, userId);
        if(sessionOptional.isPresent()){
         Session session = sessionOptional.get();
         if(session.getState().equals(State.INACTIVE)){
             throw new NoActiveSessionFoundException("There is no active session found for the user");
         }
         session.setState(State.INACTIVE);
         sessionRepository.save(session);

        }
        throw new NoActiveSessionFoundException("There is no active session found for the user");
    }
}