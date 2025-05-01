package com.example.auth_service.model;

import com.example.auth_service.utils.IdGenerator;
import com.example.auth_service.utils.Prefix;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Prefix("token")
@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    private String id;

    @Column(unique = true, length = 1000)
    private String accessToken;

    @Column(unique = true, length = 1000)
    private String refreshToken;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    @PreUpdate
    public void update() {
        this.updatedAt = LocalDateTime.now();
    }
}
