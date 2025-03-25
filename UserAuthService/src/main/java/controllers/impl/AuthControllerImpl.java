package controllers.impl;

import controllers.AuthController;
import dtos.LoginRequestDto;
import dtos.LoginResponseDto;
import dtos.ResponseDto;
import dtos.SignupRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements AuthController {
    @Override
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(LoginRequestDto requestDto) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponseDto>> signup(SignupRequestDto requestDto) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse<ResponseDto>> logout() {
        return null;
    }
}
