package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.user.CurrentUserDto;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.UserRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void getCurrentUser_ShouldReturnMappedUser_WhenUserExists() {
        String email = "test@test.com";

        User user = new User();
        CurrentUserDto expected = new CurrentUserDto();

        when(this.userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(this.modelMapper.map(user, CurrentUserDto.class))
                .thenReturn(expected);

        CurrentUserDto actual = this.userService.getCurrentUser(email);

        assertSame(expected, actual);

        verify(this.userRepository).findByEmail(email);
        verify(this.modelMapper).map(user, CurrentUserDto.class);
    }


    @Test
    void getCurrentUser_ShouldThrowException_WhenUserDoesNotExist() {
        String email = "missing@test.com";

        when(this.userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.userService.getCurrentUser(email)
        );

        verify(this.modelMapper, never())
                .map(any(User.class), eq(CurrentUserDto.class));
    }


    @Test
    void getUserEntityByEmail_ShouldReturnUser_WhenUserExists() {
        String email = "test@test.com";

        User expected = new User();

        when(this.userRepository.findByEmail(email))
                .thenReturn(Optional.of(expected));

        User actual = this.userService.getUserEntityByEmail(email);

        assertSame(expected, actual);

        verify(this.userRepository).findByEmail(email);
    }


    @Test
    void getUserEntityByEmail_ShouldThrowException_WhenUserDoesNotExist() {
        String email = "missing@test.com";

        when(this.userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.userService.getUserEntityByEmail(email)
        );
    }
}