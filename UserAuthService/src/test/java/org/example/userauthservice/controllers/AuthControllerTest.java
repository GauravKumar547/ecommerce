package org.example.userauthservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.config.Config;
import org.example.userauthservice.dtos.LoginRequestDto;
import org.example.userauthservice.dtos.ResponseDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.dtos.ValidateTokenDto;
import org.example.userauthservice.exceptions.InvalidCredentialsException;
import org.example.userauthservice.exceptions.NoActiveSessionFoundException;
import org.example.userauthservice.exceptions.UserAlreadyExistingException;
import org.example.userauthservice.exceptions.UserNotRegisteredException;
import org.example.userauthservice.models.Role;
import org.example.userauthservice.models.User;
import org.example.userauthservice.services.AuthService;
import org.example.userauthservice.utils.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
@Import(Config.class)
@MockitoBean(types= JpaMetamodelMappingContext.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SecretKey secretKey;

    @Test
    public void testLogin_WithValidCredentials_RunsSuccessfully() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@test.com");
        loginRequestDto.setPassword("password");
        long userId = 1L;
        User user = User.builder().id(userId).email("test@test.com").password("password").name("test").role(Role.USER).build();
        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setEmail("test@test.com");
        expectedUserDto.setName("test");
        expectedUserDto.setRole(Role.USER.toString());
        ApiResponse<UserDto> response = new ApiResponse<>();
        response.setData(expectedUserDto).setStatus(HttpStatus.OK);
        String token = Jwts.builder().signWith(secretKey).claim("userId", userId).compact();
        when(authService.login(anyString(), anyString())).thenReturn(new Pair<>(user, token));

        String expectedTokenHeader = ResponseCookie.from("token", token).build().toString();

        // Act and Assert
        mockMvc.perform(post("/auth/login").content(objectMapper.writeValueAsString(loginRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
                .andExpect(header().string("Set-Cookie", expectedTokenHeader));
    }

    @Test
    public void testLogin_WithEmptyPassword_RunsSuccessfully() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@test.com");

        // Act and Assert
        mockMvc.perform(post("/auth/login").content(objectMapper.writeValueAsString(loginRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No email or password provided"));
    }

    @Test
    public void testLogin_WithEmptyEmail_RunsSuccessfully() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPassword("test@testPassword");

        // Act and Assert
        mockMvc.perform(post("/auth/login").content(objectMapper.writeValueAsString(loginRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No email or password provided"));
    }
    @Test
    public void testLogin_WithEmptyDetails_RunsSuccessfully() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        // Act and Assert
        mockMvc.perform(post("/auth/login").content(objectMapper.writeValueAsString(loginRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No email or password provided"));
    }

    @Test
    public void testLogin_WithInvalidEmail_RunsSuccessfully() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@test.com");
        loginRequestDto.setPassword("password");
        when(authService.login(anyString(), anyString())).thenThrow(
                new UserNotRegisteredException("User with given email does not exist")
        );
        mockMvc.perform(post("/auth/login").content(objectMapper.writeValueAsString(loginRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User with given email does not exist"));

    }
    @Test
    public void testLogin_WithInvalidCredentials_RunsSuccessfully() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@test.com");
        loginRequestDto.setPassword("password");
        when(authService.login(anyString(), anyString())).thenThrow(
                new InvalidCredentialsException("Invalid Credentials")
        );
        mockMvc.perform(post("/auth/login").content(objectMapper.writeValueAsString(loginRequestDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Credentials"));
    }

    @Test
    public void testSignup_WithValidUserDetails_RunsSuccessfully() throws Exception{
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setPassword("password");
        userDto.setName("test");
        userDto.setRole(Role.USER.toString());

        when(authService.signup(any(User.class))).thenReturn(true);
        ApiResponse<ResponseDto> responseDto = new ApiResponse<>();
        ResponseDto res = new ResponseDto();
        res.setMessage("User registered successfully");
        responseDto.setStatus(HttpStatus.CREATED).setData(res);

        // Act and Assert
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    public void testSignup_WithEmptyEmail_RunsSuccessfully() throws Exception{
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setName("test");
        userDto.setRole(Role.USER.toString());

        // Act and Assert
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Required parameters not provided"));
    }
    @Test
    public void testSignup_WithEmptyName_RunsSuccessfully() throws Exception{
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setEmail("test@test.com");
        userDto.setRole(Role.USER.toString());

        // Act and Assert
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Required parameters not provided"));
    }
    @Test
    public void testSignup_WithEmptyDetails_RunsSuccessfully() throws Exception{
        // Arrange
        UserDto userDto = new UserDto();

        // Act and Assert
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Required parameters not provided"));
    }
    @Test
    public void testSignup_WithExstingEmail_RunsSuccessfully() throws Exception{
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setPassword("password");
        userDto.setEmail("test@test.com");
        userDto.setName("test");
        userDto.setRole(Role.USER.toString());
        when(authService.signup(any(User.class))).thenThrow(new UserAlreadyExistingException("User already exists with this email"));

        // Act and Assert
        mockMvc.perform(post("/auth/signup").content(objectMapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User already exists with this email"));
    }
    @Test
    public void testLogout_WithValidUserId_RunsSuccessfully() throws Exception{
        // Arrange
        long userId = 1L;
        String token = Jwts.builder().signWith(secretKey).claim("userId", userId).compact();
        String cookie = "token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("User logged out successfully");
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        apiResponse.setData(responseDto).setStatus(HttpStatus.OK);
        // Act and Assert
        mockMvc.perform(post("/auth/logout/{id}",userId).contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));

    }
    @Test
    public void testLogout_WithNoValidUserIdSession_RunsSuccessfully() throws Exception{
        // Arrange
        long userId = 1L;
        String token = Jwts.builder().signWith(secretKey).claim("userId", userId).compact();
        String cookie = "token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie);
        doThrow(new NoActiveSessionFoundException("There is no active session found for the user")).when(authService).logout(anyString(), anyLong());

        // Act and Assert
        mockMvc.perform(post("/auth/logout/{id}",userId).contentType(MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("There is no active session found for the user"));

    }

    @Test
    public void testValidateToken_WithValidTokenAndUserId_RunsSuccessfully() throws Exception{
        // Arrange
        long userId = 1L;
        String token = Jwts.builder().signWith(secretKey).claim("userId", userId).compact();
        ValidateTokenDto validateTokenDto = new ValidateTokenDto();
        validateTokenDto.setUserId(userId);
        validateTokenDto.setToken(token);
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Token validated successfully");
        apiResponse.setStatus(HttpStatus.OK).setData(responseDto);
        when(authService.validateToken(anyString(), anyLong())).thenReturn(true);
        // Act and Assert
        mockMvc.perform(post("/auth/validate_token").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validateTokenDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));

    }
    @Test
    public void testValidateToken_WithInvalidTokenAndUserId_RunsSuccessfully() throws Exception{
        // Arrange
        long userId = 1L;
        ValidateTokenDto validateTokenDto = new ValidateTokenDto();
        validateTokenDto.setUserId(userId);
        // Act and Assert
        mockMvc.perform(post("/auth/validate_token").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validateTokenDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Token not provided"));
    }
    @Test
    public void testValidateToken_WithValidTokenAndInvalidUserId_RunsSuccessfully() throws Exception{
        // Arrange
        long userId = -1L;
        String token = Jwts.builder().signWith(secretKey).claim("userId", userId).compact();
        ValidateTokenDto validateTokenDto = new ValidateTokenDto();
        validateTokenDto.setUserId(userId);
        validateTokenDto.setToken(token);
        // Act and Assert
        mockMvc.perform(post("/auth/validate_token").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validateTokenDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User id not provided"));
    }
    @Test
    public void testValidateToken_WithValidTokenAndDifferentUserId_RunsSuccessfully() throws Exception{
        // Arrange
        long userId = 1L;
        long differentUserId = 2L;
        String token = Jwts.builder().signWith(secretKey).claim("userId", userId).compact();
        ValidateTokenDto validateTokenDto = new ValidateTokenDto();
        validateTokenDto.setUserId(differentUserId);
        validateTokenDto.setToken(token);
        ApiResponse<ResponseDto> apiResponse = new ApiResponse<>();
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Invalid token");
        apiResponse.setStatus(HttpStatus.UNAUTHORIZED).setData(responseDto);
        when(authService.validateToken(anyString(), anyLong())).thenReturn(false);
        // Act and Assert
        mockMvc.perform(post("/auth/validate_token").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validateTokenDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(objectMapper.writeValueAsString(apiResponse)));

    }
}
