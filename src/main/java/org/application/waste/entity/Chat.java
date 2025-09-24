package org.application.waste.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ChatMessages")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "UserId",nullable = true)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Column(name = "Prompt", columnDefinition = "TEXT")
    private String prompt;

    @NotNull
    @Column(name = "Message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "ResponseDate")
    private LocalDateTime responseDate;


}

