package com.vittig.tech_nova.data.dto.user;

import com.vittig.tech_nova.data.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserDto {
    private String email;
    private UserRole role;
}
