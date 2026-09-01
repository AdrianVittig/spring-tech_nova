package com.vittig.tech_nova.controller;

import com.vittig.tech_nova.data.dto.user.AuthResponseDto;
import com.vittig.tech_nova.data.dto.user.LoginDto;
import com.vittig.tech_nova.data.dto.user.RegisterUserDto;
import com.vittig.tech_nova.service.contract.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@Valid @RequestBody RegisterUserDto registerUserDto){
        return this.authService.register(registerUserDto);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginDto loginDto){
        return this.authService.login(loginDto);
    }
}
