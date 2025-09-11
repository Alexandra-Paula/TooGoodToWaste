package org.application.waste.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(unique = true, nullable = false, name = "username", length = 50)
    private String username;

    @Column(nullable = false, name = "password", length = 60)
    private String password;

    @Column(nullable = false, name  = "email", length = 45)
    private String email;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column
    private LocalDateTime deleteDate= null;



}