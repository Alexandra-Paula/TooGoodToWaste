package org.application.waste.service;

import org.application.waste.dto.ReviewDto;
import org.application.waste.entity.Product;
import org.application.waste.entity.Review;
import org.application.waste.entity.User;
import org.application.waste.repository.ProductRepository;
import org.application.waste.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void saveReview(ReviewDto reviewDto, User user) {
        Product product = productRepository.findById(reviewDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReview(reviewDto.getReview());
        review.setRating(reviewDto.getRating());
        review.setDatePosted(LocalDateTime.now());

        reviewRepository.save(review);
    }

    @Override
    public void updateReview(ReviewDto reviewDto) {
        if (reviewDto.getId() == null) {
            throw new IllegalArgumentException("ID-ul recenziei este necesar pentru actualizare");
        }

        Review review = reviewRepository.findById(reviewDto.getId())
                .orElseThrow(() -> new RuntimeException("Recenzia nu a fost găsită"));

        review.setReview(reviewDto.getReview());
        review.setRating(reviewDto.getRating());
        review.setDatePosted(LocalDateTime.now());

        reviewRepository.save(review);
    }

    @Override
    public List<ReviewDto> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        List<ReviewDto> reviewDtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewDto dto = new ReviewDto();
            dto.setId(review.getId());
            dto.setProductId(review.getProduct().getId());
            dto.setRating(review.getRating());
            dto.setReview(review.getReview());
            dto.setDatePosted(review.getDatePosted());
            dto.setReviewerName(review.getUser().getUsername());
            dto.setUserImage("/images/assets/user.png");
            reviewDtos.add(dto);
        }

        return reviewDtos;
    }

    @Override
    public void deleteReviewById(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Recenzia nu a fost găsită cu id-ul: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    public Optional<ReviewDto> getReviewById(Long id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            return Optional.empty();
        }

        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setProductId(review.getProduct().getId());
        dto.setRating(review.getRating());
        dto.setReview(review.getReview());

        return Optional.of(dto);
    }

    @Override
    public void updateProductRating(Long productId) {
        Double avgRating = reviewRepository.findAverageRatingByProductId(productId);
        if (avgRating == null) {
            avgRating = 0.0;
        }

        int roundedRating = (int) Math.round(avgRating);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit"));

        product.setRating(roundedRating);
        productRepository.save(product);
    }
}