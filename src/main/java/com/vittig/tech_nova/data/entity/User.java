package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;
    @Column(unique = true, nullable = false)
    private String email;
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
