package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.util.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtServiceImpl jwtService;


    @Test
    void generateToken_ShouldReturnEncodedToken() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUserRole(UserRole.CUSTOMER);

        Jwt jwt = Jwt.withTokenValue("generated-jwt-token")
                .header("alg", "HS256")
                .subject(user.getEmail())
                .claim("role", UserRole.CUSTOMER.name())
                .build();

        when(this.jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        String actual = this.jwtService.generateToken(user);

        assertEquals("generated-jwt-token", actual);

        verify(this.jwtEncoder)
                .encode(any(JwtEncoderParameters.class));
    }


    @Test
    void generateToken_ShouldCreateCorrectClaims() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUserRole(UserRole.CUSTOMER);

        Jwt jwt = Jwt.withTokenValue("generated-jwt-token")
                .header("alg", "HS256")
                .subject(user.getEmail())
                .build();

        when(this.jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        this.jwtService.generateToken(user);

        verify(this.jwtEncoder).encode(captor.capture());

        JwtEncoderParameters parameters = captor.getValue();
        JwtClaimsSet claims = parameters.getClaims();

        assertEquals(
                "test@test.com",
                claims.getSubject()
        );

        assertEquals(
                UserRole.CUSTOMER.name(),
                claims.getClaim("role")
        );

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());
    }


    @Test
    void generateToken_ShouldExpireAfterApproximatelyFiveMinutes() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setUserRole(UserRole.CUSTOMER);

        Jwt jwt = Jwt.withTokenValue("generated-jwt-token")
                .header("alg", "HS256")
                .subject(user.getEmail())
                .build();

        when(this.jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        this.jwtService.generateToken(user);

        verify(this.jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();

        assertNotNull(issuedAt);
        assertNotNull(expiresAt);

        Duration tokenLifetime = Duration.between(issuedAt, expiresAt);

        assertEquals(300, tokenLifetime.getSeconds());

        assertEquals(300, tokenLifetime.getSeconds());
    }


    @Test
    void generateToken_ShouldUseUserRoleAsRoleClaim() {
        User user = new User();
        user.setEmail("admin@test.com");
        user.setUserRole(UserRole.ADMIN);

        Jwt jwt = Jwt.withTokenValue("admin-token")
                .header("alg", "HS256")
                .subject(user.getEmail())
                .build();

        when(this.jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        String actual = this.jwtService.generateToken(user);

        verify(this.jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("admin-token", actual);
        assertEquals("admin@test.com", claims.getSubject());
        assertEquals(
                UserRole.ADMIN.name(),
                claims.getClaim("role")
        );

        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();

        assertNotNull(issuedAt);
        assertNotNull(expiresAt);
        assertTrue(expiresAt.isAfter(issuedAt));
    }
}