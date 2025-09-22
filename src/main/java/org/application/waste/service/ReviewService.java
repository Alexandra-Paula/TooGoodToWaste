package org.application.waste.service;

import org.application.waste.dto.ReviewDto;
import org.application.waste.entity.User;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    void saveReview(ReviewDto reviewDto, User user);

    List<ReviewDto> getReviewsByProductId(Long productId);

    void deleteReviewById(Long id);

    Optional<ReviewDto> getReviewById(Long id);

    void updateReview(ReviewDto reviewDto);

    void updateProductRating(Long productId);
}