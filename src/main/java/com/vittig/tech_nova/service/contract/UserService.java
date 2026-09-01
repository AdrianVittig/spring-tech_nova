package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.user.CurrentUserDto;
import com.vittig.tech_nova.data.entity.User;

public interface UserService {
    CurrentUserDto getCurrentUser(String email);
    User getUserEntityByEmail(String email);
}
