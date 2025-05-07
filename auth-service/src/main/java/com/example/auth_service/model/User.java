package com.example.auth_service.model;

import com.example.auth_service.enums.Role;
import com.example.auth_service.utils.IdGenerator;
import com.example.auth_service.utils.Prefix;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Prefix("usr")
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "is_logged_out")
    private boolean isLoggedOut;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Token> tokens;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void init() {

        if (this.id == null) {
            this.id = IdGenerator.generateId(this);
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
