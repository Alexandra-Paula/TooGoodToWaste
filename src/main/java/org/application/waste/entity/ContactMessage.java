package org.application.waste.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(nullable=false, length=150)
    private String email;

    @Column(nullable=false, length=150)
    private String title;

    @Lob
    @Column(nullable=false)
    private String message;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Status status = Status.NEW;

    public enum Status { NEW, READ, ANSWERED }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
