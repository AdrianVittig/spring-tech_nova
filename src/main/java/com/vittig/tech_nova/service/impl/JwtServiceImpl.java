    package com.vittig.tech_nova.service.impl;

    import com.vittig.tech_nova.data.entity.User;
    import com.vittig.tech_nova.service.contract.JwtService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.oauth2.jwt.JwtClaimsSet;
    import org.springframework.security.oauth2.jwt.JwtEncoder;
    import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
    import org.springframework.stereotype.Service;

    import java.time.Instant;

    @Service
    @RequiredArgsConstructor
    public class JwtServiceImpl implements JwtService {
        private final JwtEncoder jwtEncoder;
        @Override
        public String generateToken(User user) {
            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .subject(user.getEmail()).issuedAt(now)
                    .expiresAt(now.plusSeconds(300))
                    .claim("role", user.getUserRole().name())
                    .build();

            return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }
    }
