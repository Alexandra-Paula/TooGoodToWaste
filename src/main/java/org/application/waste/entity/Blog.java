package org.application.waste.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titlul este obligatoriu")
    @Column(name = "title", length = 500)
    private String title;

    @NotBlank(message = "Conținutul nu poate fi gol")

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "imageUrl", columnDefinition = "TEXT")
    private String imageUrl;

    private LocalDateTime createdAt;
}
