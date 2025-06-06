package com.ecommerce.services;

import java.util.List;

import com.ecommerce.models.Review;

public interface ReviewService {
	Review saveReview(int pid,Review review,int customerId);
	
	int calculateRating(int productId);
	
	List<Review> getAllReviews();
	
	boolean deleteReview(int reviewId);
}