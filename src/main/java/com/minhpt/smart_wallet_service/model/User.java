package com.minhpt.smart_wallet_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new LinkedHashSet<>();

    @Column(name = "verified")
    private int verified;

    @Column(name = "premium_expired_at")
    private LocalDateTime premiumExpiredAt;

    public Set<String> getRoleNames() {
        Set<String> roleNames = userRoles.stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return roleNames;
    }

    public void addRole(Role role) {
        if (role == null || role.getId() == null) {
            return;
        }

        boolean alreadyAssigned = userRoles.stream()
                .anyMatch(userRole -> userRole.getRole() != null && role.getId().equals(userRole.getRole().getId()));

        if (alreadyAssigned) {
            return;
        }

        userRoles.add(
                UserRole.builder()
                        .id(new UserRoleId(this.id, role.getId()))
                        .user(this)
                        .role(role)
                        .build()
        );
    }

    public void replaceRoles(Set<Role> roles) {
        this.userRoles.clear();

        if (roles == null) {
            return;
        }

        roles.forEach(this::addRole);
    }
}
