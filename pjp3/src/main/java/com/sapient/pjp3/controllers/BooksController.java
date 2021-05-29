package com.sapient.pjp3.controllers;


import com.sapient.pjp3.dao.BookRequestsDao;
import com.sapient.pjp3.dao.BooksDao;
import com.sapient.pjp3.dao.ReviewsDao;
import com.sapient.pjp3.entity.BookRequest;
import com.sapient.pjp3.entity.Review;
import com.sapient.pjp3.utils.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/books")
public class BooksController {
    BooksDao dao;
    BooksDao editBooksDao;
    BookRequestsDao dao_;
    
    @GetMapping("{filter}/{order}")
    public ResponseEntity<?> getBooksByFilter(@PathVariable String filter, @PathVariable String order)
    {
    	BooksDao booksDao = new BooksDao();
    	Map<String, Object> map = new HashMap<>();
    	map.put("Filter", filter);
    	map.put("Order", order);
    	map.put("TheListOfBooks", booksDao.orderBooksByFilter(filter,order));
    	
    	return ResponseEntity.ok(map);
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getBooksByGenre(@PathVariable String genre)
    {
    	BooksDao booksDao = new BooksDao();
    	Map<String, Object> map = new HashMap<>();
    	map.put("Genre", genre);
    	map.put("ListOfBooks", booksDao.getBooksByGenre(genre));
    	
    	return ResponseEntity.ok(map);
    }
    
    @GetMapping("/genre")
    public ResponseEntity<?> getAllGenres()
    {
    	BooksDao booksDao = new BooksDao();
    	Map<String, Object> map = new HashMap<>();
    	map.put("ListOfGenres", booksDao.getAllGenres());
    	
    	return ResponseEntity.ok(map);
    }
    
    @GetMapping("/mostpopular")
    public ResponseEntity<?> getMostPopularBooks()
    {
    	BooksDao booksDao = new BooksDao();
    	Map<String, Object> map = new HashMap<>();
    	map.put("ListOfBooks", booksDao.getMostPopularBooks());
    	
    	return ResponseEntity.ok(map);
    }
    
    @GetMapping("/{isbn}")
    public ResponseEntity<?> getAllGenres(@PathVariable Long isbn)
    {
    	BooksDao booksDao = new BooksDao();
    	Map<String, Object> map = new HashMap<>();
    	map.put("Book", booksDao.getBookByIsbn(isbn));
    	
    	return ResponseEntity.ok(map);
    }

	@GetMapping("/available/{isbn}")
	public ResponseEntity<?> checkIfCopiesAvailable(@PathVariable Long isbn)
	{
		BooksDao booksDao = new BooksDao();
		Map<String, Object> map = new HashMap<>();
		map.put("Availability", booksDao.checkCopyAvailability(isbn));
		return ResponseEntity.ok(map);
	}
    
    @GetMapping("/{isbn}/borrow")
    public ResponseEntity<?> borrowBook(@RequestHeader(name = "Authorization", required = false) String authHeader, 
    		@PathVariable Long isbn){
    	Logger log = LoggerFactory.getLogger(BooksController.class);
		log.info("\n Borrowing : \n authHeader = {}", authHeader);
    	if(authHeader==null) {
			// Authorization header is missing
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
		}
    	
    	try {
			String token1 = authHeader.split(" ")[1]; // second element from the header's value
			log.info("token = {}", token1);
			Integer userId1 = JwtUtil.verify(token1);
			
			log.info("THE returned", userId1);
			BooksDao booksDao = new BooksDao();
			if(booksDao.checkIfBorrowPossible(userId1)) {
				Map<String, Object> map = new HashMap<>();
				map.put("Book", booksDao.borrowBook(isbn, userId1));
				return ResponseEntity.ok(map);
			}
			else{
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Already Borrowed 5 Books");
			}
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is invalid or " + ex.getMessage());
		}
    
    }

	/**
	 * Check if Book is Borrowed by User
	 * @param authHeader
	 * @param isbn
	 * @return Boolean if book successfully returned or not
	 */
	@GetMapping("/{isbn}/checkBorrow")
	public ResponseEntity<?> checkIfBookBorrowed(@RequestHeader(name = "Authorization", required = false) String authHeader,
										@PathVariable Long isbn){
		Logger log = LoggerFactory.getLogger(BooksController.class);
		log.info("authHeader = {}", authHeader);
		if(authHeader==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
		}

		try {
			String token1 = authHeader.split(" ")[1]; // second element from the header's value
			log.info("token = {}", token1);
			Integer userId = JwtUtil.verify(token1);

			log.info("User ID", userId);
			BooksDao booksDao = new BooksDao();
			Map<String, Object> map = new HashMap<>();
			map.put("Issued", booksDao.checkIfBorrowed(isbn, userId));
			return ResponseEntity.ok(map);
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is invalid or " + ex.getMessage());
		}

	}

	/**
	 * Return Borrowed Book
	 * @param authHeader
	 * @param isbn
	 * @return Boolean if book successfully returned or not
	 */
	@GetMapping("/{isbn}/return")
	public ResponseEntity<?> returnBook(@RequestHeader(name = "Authorization", required = false) String authHeader,
										@PathVariable Long isbn){
		Logger log = LoggerFactory.getLogger(BooksController.class);
		log.info("authHeader = {}", authHeader);
		if(authHeader==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
		}

		try {
			String token1 = authHeader.split(" ")[1]; // second element from the header's value
			log.info("token = {}", token1);
			Integer userId = JwtUtil.verify(token1);

			log.info("User ID", userId);
			BooksDao booksDao = new BooksDao();

			if(booksDao.returnBook(isbn, userId)) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Book Successfully Returned");
			}
			else{
				return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Return Not Successfull");
			}
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is invalid or " + ex.getMessage());
		}

	}


    @PostMapping("/request")
    public ResponseEntity<?> getOrdersForUser(
			@RequestHeader(name = "Authorization", required = false) String authHeader, @RequestBody BookRequest request) throws Exception {
    	Logger log = LoggerFactory.getLogger(BooksController.class);
		log.info("authHeader = {}", authHeader);
		if(authHeader==null) {
			// Authorization header is missing
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
		}
		
		try {
			String token1 = authHeader.split(" ")[1]; // second element from the header's value
			log.info("token = {}", token1);
			Integer userId1 = JwtUtil.verify(token1);
			
			log.info("THE returned", userId1);
			log.info(request.getTitle());
			Map<String, Object> map = new HashMap<>();
//			BOOK_TITLE, AUTHOR, REQUESTED_AT
			map.put("success", BookRequestsDao.create(userId1,request.getTitle(),request.getAuthor()));
			map.put("user_id", userId1);
			return ResponseEntity.ok(map);
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is invalid or " + ex.getMessage());
		}
	}
    
    @GetMapping("/{isbn}/reviews")
    public ResponseEntity<?> getReviews(
    		@RequestHeader(name = "Authorization", required = false) String authHeader,
    		@PathVariable Long isbn){
    	
    	Logger log = LoggerFactory.getLogger(BooksController.class);
    	log.info("authHeader = {}", authHeader);
    	
    	BooksDao booksDao = new BooksDao();
    	ReviewsDao reviewsDao = new ReviewsDao();
    	
    	Map<String, Object> map = new HashMap<>();
    	map.put("Book", booksDao.getBookByIsbn(isbn));
    	map.put("ListOfReviews", reviewsDao.getReviewsByIsbn(isbn));
    	
    	if(authHeader!=null) {
        	try {
        		String token = authHeader.split(" ")[1]; // second element from the header's value
    			log.info("totken = {}", token);
    			Integer userId = JwtUtil.verify(token);
        		map.put("isReviewedByUser", reviewsDao.checkReviewStatus(userId, isbn));
    			
    		}
    		catch(Exception ex) {
    			map.put("isReviewedByUser", false);
    		}
		}
    	
    	return ResponseEntity.ok(map);
    }
    
    @PostMapping("/{isbn}/reviews")
    public  ResponseEntity<?> postReview(
			@RequestHeader(name = "Authorization", required = false) String authHeader,
			@PathVariable("isbn") long isbn,
			@RequestBody Review reviewRequest) {
    	Logger log = LoggerFactory.getLogger(BooksController.class);
    	log.info("authHeader = {}", authHeader);
    	
    	ReviewsDao reviewDao = new ReviewsDao();
    	
    	if(authHeader==null) {
			// Authorization header is missing
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
		}
    	try {
			String token = authHeader.split(" ")[1]; // second element from the header's value
			log.info("totken = {}", token);
			Integer userId = JwtUtil.verify(token);
			
			//reviewRequest.setReviewId(reviewDao.getMaxReviewID() + 1);
			reviewRequest.setUserId(userId);
			reviewRequest.setIsbn(isbn);
	
			log.info(reviewRequest.toString());
			
			reviewDao.addReview(reviewRequest);
			
			Map<String, Object> map = new HashMap<>();
			map.put("success", true);
			map.put("userId", userId);
			map.put("review", reviewRequest.getReview());
			map.put("rating", reviewRequest.getRating());			
			return ResponseEntity.ok(map);	
			
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is invalid or " + ex.getMessage());
		}
		
    }

	@PutMapping("/{isbn}/reviews/{reviewId}")
	public ResponseEntity<?> updateReview(
			@RequestHeader(name = "Authorization", required = false) String authHeader,
			@RequestBody Review review, @PathVariable long isbn, @PathVariable int reviewId
	) throws Exception {
		ReviewsDao reviewDao = new ReviewsDao();
		Logger log = LoggerFactory.getLogger(BooksController.class);
		log.info("authHeader = {}", authHeader);
		if(authHeader==null) {// Authorization header is missing
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing");
		}

		try {
			String token1 = authHeader.split(" ")[1]; // second element from the header's value
			log.info("token = {}", token1);
			Integer userId1 = JwtUtil.verify(token1);
			
			review.setReviewId(reviewId);
			review.setUserId(userId1);
			review.setIsbn(isbn);
			if(reviewDao.updateReview(review)) {
				Map<String, Object> map = new HashMap<>();
				map.put("success", true);
				map.put("review",review);
				return ResponseEntity.ok(map); 
			}
			
			else{ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Review could not be updated"); }
		}
		catch(Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is invalid or " + ex.getMessage());
		}
	}
	
	@GetMapping("/search/{keyword}")
	public ResponseEntity<?> getBooksByKeyword(@PathVariable String keyword)
    {
    	BookRequestsDao bookRequestsDao = new BookRequestsDao();
    	Map<String, Object> map = new HashMap<>();
    	map.put("keyword", keyword);
    	map.put("TheListOfBooks", bookRequestsDao.getBooksByKeyword(keyword));
    	
    	return ResponseEntity.ok(map);
    }


}
