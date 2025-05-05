package com.example.auth_service.model;

import com.example.auth_service.enums.Role;
import com.example.auth_service.utils.IdGenerator;
import com.example.auth_service.utils.Prefix;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
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
