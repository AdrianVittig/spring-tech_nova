package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.user.AuthResponseDto;
import com.vittig.tech_nova.data.dto.user.LoginDto;
import com.vittig.tech_nova.data.dto.user.RegisterUserDto;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.UserRepository;
import com.vittig.tech_nova.data.util.UserRole;
import com.vittig.tech_nova.service.contract.JwtService;
import com.vittig.tech_nova.service.exception.ConflictException;
import com.vittig.tech_nova.service.exception.InvalidCredentialsException;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    void register_ShouldRegisterUserAndReturnToken_WhenInputIsValid() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password123");

        when(this.userRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);

        when(this.passwordEncoder.encode(dto.getPassword()))
                .thenReturn("encoded-password");

        when(this.jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        AuthResponseDto actual = this.authService.register(dto);

        assertEquals("test@test.com", actual.getEmail());
        assertEquals("jwt-token", actual.getAccessToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(this.userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("test@test.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPasswordHash());
        assertEquals(UserRole.CUSTOMER, savedUser.getUserRole());

        verify(this.passwordEncoder).encode("password123");
        verify(this.jwtService).generateToken(savedUser);
    }


    @Test
    void register_ShouldThrowException_WhenDtoIsNull() {
        assertThrows(
                InvalidInputException.class,
                () -> this.authService.register(null)
        );

        verify(this.userRepository, never())
                .save(any(User.class));

        verify(this.jwtService, never())
                .generateToken(any(User.class));
    }


    @Test
    void register_ShouldThrowException_WhenEmailIsNull() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setPassword("password123");

        assertThrows(
                InvalidInputException.class,
                () -> this.authService.register(dto)
        );

        verify(this.userRepository, never())
                .existsByEmail(any());

        verify(this.userRepository, never())
                .save(any(User.class));
    }


    @Test
    void register_ShouldThrowException_WhenPasswordIsNull() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("test@test.com");

        assertThrows(
                InvalidInputException.class,
                () -> this.authService.register(dto)
        );

        verify(this.userRepository, never())
                .existsByEmail(any());

        verify(this.userRepository, never())
                .save(any(User.class));
    }


    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password123");

        when(this.userRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> this.authService.register(dto)
        );

        verify(this.passwordEncoder, never())
                .encode(any());

        verify(this.userRepository, never())
                .save(any(User.class));

        verify(this.jwtService, never())
                .generateToken(any(User.class));
    }


    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("password123");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPasswordHash("encoded-password");
        user.setUserRole(UserRole.CUSTOMER);

        when(this.userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(this.passwordEncoder.matches(
                dto.getPassword(),
                user.getPasswordHash()
        )).thenReturn(true);

        when(this.jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponseDto actual = this.authService.login(dto);

        assertEquals("test@test.com", actual.getEmail());
        assertEquals("jwt-token", actual.getAccessToken());

        verify(this.userRepository).findByEmail("test@test.com");

        verify(this.passwordEncoder)
                .matches("password123", "encoded-password");

        verify(this.jwtService).generateToken(user);
    }


    @Test
    void login_ShouldThrowException_WhenDtoIsNull() {
        assertThrows(
                InvalidInputException.class,
                () -> this.authService.login(null)
        );

        verify(this.userRepository, never())
                .findByEmail(any());

        verify(this.jwtService, never())
                .generateToken(any(User.class));
    }


    @Test
    void login_ShouldThrowException_WhenEmailIsNull() {
        LoginDto dto = new LoginDto();
        dto.setPassword("password123");

        assertThrows(
                InvalidInputException.class,
                () -> this.authService.login(dto)
        );

        verify(this.userRepository, never())
                .findByEmail(any());
    }


    @Test
    void login_ShouldThrowException_WhenPasswordIsNull() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");

        assertThrows(
                InvalidInputException.class,
                () -> this.authService.login(dto)
        );

        verify(this.userRepository, never())
                .findByEmail(any());
    }


    @Test
    void login_ShouldThrowException_WhenUserDoesNotExist() {
        LoginDto dto = new LoginDto();
        dto.setEmail("missing@test.com");
        dto.setPassword("password123");

        when(this.userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> this.authService.login(dto)
        );

        verify(this.passwordEncoder, never())
                .matches(any(), any());

        verify(this.jwtService, never())
                .generateToken(any(User.class));
    }


    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
        LoginDto dto = new LoginDto();
        dto.setEmail("test@test.com");
        dto.setPassword("wrong-password");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPasswordHash("encoded-password");

        when(this.userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(user));

        when(this.passwordEncoder.matches(
                dto.getPassword(),
                user.getPasswordHash()
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> this.authService.login(dto)
        );

        verify(this.jwtService, never())
                .generateToken(any(User.class));
    }
}