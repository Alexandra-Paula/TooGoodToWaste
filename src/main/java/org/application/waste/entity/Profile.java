package org.application.waste.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_profile_user", columnNames = {"user_id"})
})
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Legătura 1-1 cu User (fără să modificăm entitatea User)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    // ----- ACCOUNT SETTINGS -----
    @Column(name = "first_name", length = 60)
    private String firstName;

    @Column(name = "last_name", length = 60)
    private String lastName;

    // Email de contact din profil (poate fi sincronizat cu User.email la update)
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    // ----- BILLING ADDRESS -----
    @Column(name = "billing_first_name", length = 60)
    private String billingFirstName;

    @Column(name = "billing_last_name", length = 60)
    private String billingLastName;

    @Column(name = "company_name", length = 120)
    private String companyName;

    @Column(name = "street_address", length = 200)
    private String streetAddress;

    @Column(name = "country", length = 80)
    private String country;

    @Column(name = "state", length = 80)
    private String state;


    @Column(name = "billing_email", length = 100)
    private String billingEmail;

    @Column(name = "billing_phone", length = 32)
    private String billingPhone;

    // Audit minimal
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
