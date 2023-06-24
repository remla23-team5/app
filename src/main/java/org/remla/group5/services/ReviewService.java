package org.remla.group5.services;

import org.remla.group5.models.Review;
import org.remla.group5.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review addReview(Review review) throws RuntimeException {
        Long reviewId = review.getId();

        if (reviewId != null && reviewRepository.existsById(reviewId)) {
            throw new RuntimeException(String.format("A review with ID %d already exists in the system.", reviewId));
        }

        return reviewRepository.saveAndFlush(review);
    }

    public Review updateReview(Review review) throws RuntimeException {
        Long reviewId = review.getId();

        if (reviewId != null && !reviewRepository.existsById(reviewId)) {
            throw new RuntimeException(String.format("A review with ID %d does not exist in the system.", reviewId));
        }

        return reviewRepository.saveAndFlush(review);
    }
}
