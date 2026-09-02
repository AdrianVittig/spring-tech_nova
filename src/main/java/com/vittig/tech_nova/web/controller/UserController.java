package com.vittig.tech_nova.web.controller;

import com.vittig.tech_nova.data.dto.user.CurrentUserDto;
import com.vittig.tech_nova.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    public CurrentUserDto getCurrentUser(Authentication authentication){
        return this.userService.getCurrentUser(authentication.getName());
    }
}
