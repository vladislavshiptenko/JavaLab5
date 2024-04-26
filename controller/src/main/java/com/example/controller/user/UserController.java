package com.example.controller.user;

import com.example.dto.user.SignInDto;
import com.example.dto.user.SignUpDto;
import com.example.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@Valid @RequestBody SignUpDto signUpDto) {
        try {
            return ResponseEntity.ok(authenticationService.signUp(signUpDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auth")
    public String getUser(@Valid @RequestBody SignInDto signInDto) {
        return authenticationService.signIn(signInDto);
    }
}
