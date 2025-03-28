package org.example.userauthservice.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.exceptions.*;
import org.example.userauthservice.models.Session;
import org.example.userauthservice.models.User;
import org.example.userauthservice.repos.SessionRepository;
import org.example.userauthservice.repos.UserRepository;
import org.example.userauthservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotRegisteredException("User with given email does not exist");
        }
        User user = optionalUser.get();
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        List<Session> sessions = sessionRepository.findAllByUserId(user.getId());


        Map<String, Object> payload = new HashMap<>();
        long currentMillis = System.currentTimeMillis();
        payload.put("user_id", user.getId());
        payload.put("iat", currentMillis);
        payload.put("exp", currentMillis + 3600000);
        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();
        if (!sessions.isEmpty()) {
         sessionRepository.deleteAll(sessions);
        }
        Session session = Session.builder().token(token).user(user).build();
        sessionRepository.save(session);
        return new Pair<>(user, token);
    }

    @Override
    public Boolean signup(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistingException("User already exists with this email");
        }
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        userRepository.save(user);
        return true;
    }

    @Override
    public void logout(String token, long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUserId(token, userId);
        sessionOptional.ifPresent(sessionRepository::delete);
        throw new NoActiveSessionFoundException("There is no active session found for the user");
    }

    @Override
    public Boolean validateToken(String token, long userId) {
        long currentMillis = System.currentTimeMillis();
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUserId(token, userId);
        if(sessionOptional.isEmpty()){
            return false;
        }
        Session session = sessionOptional.get();
        try{
            JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
            Claims claims = parser.parseSignedClaims(token).getPayload();

            Long exp = claims.get("exp", Long.class);
            Long uid = claims.get("user_id", Long.class);
            if(!uid.equals(userId)){
                throw new UserUnauthorizedException("Invalid user");
            }
            if(exp < currentMillis){
                sessionRepository.delete(session);
                return false;
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}