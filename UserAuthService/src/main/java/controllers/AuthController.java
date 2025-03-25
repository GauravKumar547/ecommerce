package controllers;

import dtos.LoginRequestDto;
import dtos.LoginResponseDto;
import dtos.ResponseDto;
import dtos.SignupRequestDto;
import org.springframework.http.ResponseEntity;
import utils.ApiResponse;

public interface AuthController {
    ResponseEntity<ApiResponse<LoginResponseDto>> login(LoginRequestDto requestDto);
    ResponseEntity<ApiResponse<LoginResponseDto>> signup(SignupRequestDto requestDto);
    ResponseEntity<ApiResponse<ResponseDto>> logout();

}
