package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.entity.User;

public interface JwtService {
    String generateToken(User user);
}
