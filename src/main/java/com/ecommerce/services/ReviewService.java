package com.ecommerce.services;

import java.util.List;

import com.ecommerce.models.Review;

public interface ReviewService {
	void saveReview(int pid,Review review,String customerEmail);
	
	int calculateRating(int productId);
	
	List<Review> getAllReviews();
	
	boolean deleteReview(int reviewId);
}
