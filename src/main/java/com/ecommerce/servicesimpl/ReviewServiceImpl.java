package com.ecommerce.servicesimpl;

import com.ecommerce.models.Customer;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;
import com.ecommerce.services.ReviewService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.CustomerDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.dao.ReviewDao;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ProductDao productDao;
	@Autowired
	private ReviewDao reviewDao;
	@Autowired
	private CustomerDao customerDao;

	@Override
	public void saveReview(int pid, Review review, String customerEmail) {
		Product product = this.productDao.findById(pid).get();
		Customer customer = this.customerDao.findByEmail(customerEmail);
		review.setProduct(product);
		review.setDate(LocalDate.now());
		review.setCustomer(customer);
		customer.getReviews().add(review);
		product.getReviews().add(review);
		productDao.save(product);
		customerDao.save(customer);

//		return this.reviewDao.save(review);
	}

	@Override
	public int calculateRating(int productId) {
		List<Review> reviews = this.productDao.findById(productId).map(Product::getReviews)
				.orElse(Collections.emptyList());
		int length = reviews.size();
		System.out.println("The size is reviews.size" + length);

		if (length == 0) {
			return 0;
		}

		int sum = 0;
		for (Review review : reviews) {
			sum += review.getRating();
		}
		System.out.println(sum / length);
		return sum / length;
	}

	@Override
	public List<Review> getAllReviews() {

		return this.reviewDao.findAll();
	}

	@Override
	public boolean deleteReview(int reviewId) {

		if (this.reviewDao.existsById(reviewId)) {
			this.reviewDao.deleteById(reviewId);
			return true;
		}
		return false;
	}

}
