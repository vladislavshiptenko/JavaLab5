package com.example.services.auth;

import com.example.dto.user.SignInDto;
import com.example.dto.user.SignUpDto;

public interface AuthenticationService {
    String signUp(SignUpDto signUpDto);
    String signIn(SignInDto signInDto);
}
