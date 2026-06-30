package com.ELSystem.elsystem.controllers;

import com.ELSystem.elsystem.dto.request.LoginRequest;
import com.ELSystem.elsystem.dto.request.UserRequest;
import com.ELSystem.elsystem.dto.response.AuthResponse;
import com.ELSystem.elsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRequest request){
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request){
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }
}
