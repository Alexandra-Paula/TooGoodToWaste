package org.application.waste.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BlogDto {
    private Long id;

    @NotBlank(message = "Titlul este obligatoriu")
    private String title;

    @NotBlank(message = "Conținutul nu poate fi gol")
    private String content;

    private String imageUrl;

    private MultipartFile imageFile;
}
