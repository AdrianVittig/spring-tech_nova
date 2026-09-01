package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.user.AuthResponseDto;
import com.vittig.tech_nova.data.dto.user.LoginDto;
import com.vittig.tech_nova.data.dto.user.RegisterUserDto;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.UserRepository;
import com.vittig.tech_nova.data.util.UserRole;
import com.vittig.tech_nova.service.contract.AuthService;
import com.vittig.tech_nova.service.contract.JwtService;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterUserDto dto) {
        if(dto == null){
            throw new ObjectNotFoundException("Not valid input!");
        }
        if(dto.getEmail() == null){
            throw new ObjectNotFoundException("Not valid input!");
        }
        if(dto.getPassword() == null){
            throw new ObjectNotFoundException("Not valid input!");
        }
        if(this.userRepository.existsByEmail(dto.getEmail())){
            throw new InvalidStatusException("Account already exists!");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUserRole(UserRole.CUSTOMER);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        this.userRepository.save(user);
        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setAccessToken(this.jwtService.generateToken(user));
        responseDto.setEmail(user.getEmail());
        return responseDto;
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginDto dto) {
        if(dto == null){
            throw new ObjectNotFoundException("Not valid input!");
        }
        if(dto.getEmail() == null){
            throw new ObjectNotFoundException("Not valid input!");
        }
        if(dto.getPassword() == null){
            throw new ObjectNotFoundException("Not valid input!");
        }
        User user = this.userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new ObjectNotFoundException("User with this email does not exist")
        );
        if(!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())){
            throw new InvalidStatusException("Passwords don't match!");
        }

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setAccessToken(this.jwtService.generateToken(user));
        responseDto.setEmail(user.getEmail());
        return responseDto;
    }
}
