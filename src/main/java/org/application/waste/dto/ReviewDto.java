package org.application.waste.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {
    private Long id;

    private Long productId;

    private LocalDateTime datePosted;

    private int rating;

    private String review;

    private String currentUserName;

    private String currentUserEmail;

    private String reviewerName;

    private String userImage;
}