package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.user.CurrentUserDto;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.UserRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.contract.UserService;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapperUtil modelMapper;

    @Override
    public CurrentUserDto getCurrentUser(String email) {
        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> new ObjectNotFoundException("User not found.")
        );
        return modelMapper.map(user, CurrentUserDto.class);
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(
                () -> new ObjectNotFoundException("User not found.")
        );
    }
}
