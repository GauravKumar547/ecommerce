package org.example.userauthservice.services;

import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.exceptions.*;
import org.example.userauthservice.models.Role;
import org.example.userauthservice.models.Session;
import org.example.userauthservice.models.User;
import org.example.userauthservice.repos.SessionRepository;
import org.example.userauthservice.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserAuthServiceTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private SecretKey secretKey;

    @MockitoBean
    private SessionRepository sessionRepository;

    @MockitoBean
    private UserRepository userRepository;



    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testLogin_WithValidEmailAndPassword_RunsSuccessfully() {
        // Arrange
        String password = "password";
        String email = "email@email.com";
        User user = User.builder().email(email).name("test").password(passwordEncoder.encode(password)).role(Role.USER).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        // Act
        Pair<User, String> result = authService.login(email, password);

        // Assert
        assertNotNull(result);
        assertNotNull(result.a);
        assertNotNull(result.b);
        assertEquals(email, result.a.getEmail());
        assertFalse(result.b.isEmpty());
    }
    @Test
    public void testLogin_WithValidEmailAndInvalidPassword_ThrowInvalidCredentialException() {
        // Arrange
        String password = "password";
        String email = "email@email.com";
        User user = User.builder().email(email).name("test").password(passwordEncoder.encode(password)).role(Role.USER).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        // Act and Assert
        InvalidCredentialsException invalidCredentialsException = assertThrows(InvalidCredentialsException.class, ()->authService.login(email, "Chrome@123"));

        assertEquals("Invalid Credentials", invalidCredentialsException.getMessage());
    }
    @Test
    public void testLogin_WithInvalidValidEmailAndPassword_ThrowsUserNotRegisteredException() {
        // Act and Assert
        UserNotRegisteredException expectedException = assertThrows(UserNotRegisteredException.class, ()->authService.login("test@gmail.com", "Chrome@123"));

        assertEquals("User with given email does not exist", expectedException.getMessage());
    }

    @Test
    public void testSignup_WithValidUser_RunsSuccessfully(){
        // Arrange
        User user = User.builder().name("User").email("test@gmail.com").password("testing").role(Role.USER).build();

        // Act
        Boolean result = authService.signup(user);

        // Assert
        assertTrue(result);
    }
    @Test
    public void testSignup_WithExistingUser_ThrowsUserAlreadyExistingException(){
        // Arrange
        User user = User.builder().name("User").email("test@gmail.com").password("testing").role(Role.USER).build();
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act and Assert
        UserAlreadyExistingException expectedException = assertThrows(UserAlreadyExistingException.class, ()->authService.signup(user));
        assertEquals("User already exists with this email", expectedException.getMessage());
    }

    @Test
    public void testLogout_WithValidTokenAndUserID_RunsSuccessfully(){
        // Arrange
        long userId = 1L;
        Session session = Session.builder().token("testToken").build();
        when(sessionRepository.findByTokenAndUserId(anyString(), anyLong())).thenReturn(Optional.ofNullable(session));

        // Act
        authService.logout("testToken", userId);
        // Assert
        assertTrue(true);
    }

    @Test
    public void testLogout_WithUserIdWithoutSession_RunsSuccessfully(){
        // Act and Assert
        NoActiveSessionFoundException expectedException = assertThrows(NoActiveSessionFoundException.class, ()-> authService.logout("testToken", 1L));
        assertEquals("There is no active session found for the user", expectedException.getMessage());
    }
    @Test
    public void testValidateToken_WithValidTokenAndUserId_RunsSuccessfully() {
        // Arrange
        long userId = 1L;
        Map<String, Object> payload = Map.of("user_id", userId, "iat", System.currentTimeMillis(), "exp", System.currentTimeMillis() + 3600000);
        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();

        User user = User.builder().id(userId).name("test").build();
        Session session = Session.builder().token(token).user(user).build();

        when(sessionRepository.findByTokenAndUserId(anyString(), anyLong())).thenReturn(Optional.ofNullable(session));
        // Act
        Boolean result = authService.validateToken(token, userId);
        // Assert
        assertTrue(result);
    }
    @Test
    public void testValidateToken_WithNoActiveSessionForUser_RunsSuccessfully() {
        // Arrange
        long userId = 1L;
        Map<String, Object> payload = Map.of("user_id", userId, "iat", System.currentTimeMillis(), "exp", System.currentTimeMillis() + 3600000);
        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();

        // Act
        Boolean result = authService.validateToken(token, userId);
        // Assert
        assertFalse(result);
    }
    @Test
    public void testValidateToken_WithInvalidUserId_RunsSuccessfully() {
        // Arrange
        long userId = 1L;
        Map<String, Object> payload = Map.of("user_id", userId, "iat", System.currentTimeMillis(), "exp", System.currentTimeMillis() + 3600000);
        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();

        User user = User.builder().id(userId).name("test").build();
        Session session = Session.builder().token(token).user(user).build();

        when(sessionRepository.findByTokenAndUserId(anyString(), anyLong())).thenReturn(Optional.ofNullable(session));
        // Act and Assert
        UserUnauthorizedException expectedException = assertThrows(UserUnauthorizedException.class, ()->authService.validateToken(token, 2L));
        assertEquals("Invalid user", expectedException.getMessage());
    }

    @Test
    public void testValidateToken_WithExpiredTokenAndValidUserId_RunsSuccessfully() {
        // Arrange
        long userId = 1L;
        Map<String, Object> payload = Map.of("user_id", userId, "iat", System.currentTimeMillis()-3700000, "exp", System.currentTimeMillis() - 100000);
        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();

        User user = User.builder().id(userId).name("test").build();
        Session session = Session.builder().token(token).user(user).build();

        when(sessionRepository.findByTokenAndUserId(anyString(), anyLong())).thenReturn(Optional.ofNullable(session));
        // Act
        Boolean result = authService.validateToken(token, userId);

        // Assert
        assertFalse(result);
    }

}
