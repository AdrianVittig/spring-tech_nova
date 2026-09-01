package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.user.AuthResponseDto;
import com.vittig.tech_nova.data.dto.user.LoginDto;
import com.vittig.tech_nova.data.dto.user.RegisterUserDto;

public interface AuthService {
    AuthResponseDto register(RegisterUserDto dto);
    AuthResponseDto login(LoginDto dto);
}
