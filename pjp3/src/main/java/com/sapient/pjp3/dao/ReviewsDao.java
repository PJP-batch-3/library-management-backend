package com.sapient.pjp3.dao;

import com.sapient.pjp3.entity.Review;
import com.sapient.pjp3.utils.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsDao {
	
	public int getMaxReviewID() throws SQLException, ClassNotFoundException {
		String sql = "SELECT MAX(ID) FROM REVIEWS;";
		Logger log = LoggerFactory.getLogger(ReviewsDao.class);
		Connection conn = DBUtils.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);
		log.info(stmt.toString());
		ResultSet rs =  stmt.executeQuery();   
		rs.next();
		return rs.getInt(1);
	}
	
	public Object getReviewsByUser(int userId) {
		List<Review> reviews = new ArrayList<>();

		String sql = "Select r.reviewId, r.isbn, r.rating, r.review, r.userId, r.fullName, b.title " +
				"from reviews as r inner join books as b " +
				"on r.isbn = b.isbn where r.userId = ?";
		Logger log = LoggerFactory.getLogger(BooksDao.class);

		try (Connection conn = DBUtils.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {

			stmt.setLong(1, userId);
			log.info(stmt.toString());
			ResultSet rs =  stmt.executeQuery();

			while(rs.next()) {
				Review review = resultSetToReviews(rs);
				try {
					review.setBookTitle(rs.getString("title"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				reviews.add(review);
			}

			return reviews;

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return null;

	}
	
	public Object getReviewsByIsbn(Long isbn) {
		List<Review> reviews = new ArrayList<>();
		
		String sql = "Select r.isbn, r.rating, r.review, r.userId, r.reviewId, u.fullName " +
				"from reviews as r inner join users as u " +
				"on r.userId = u.id where r.isbn = ?";
		Logger log = LoggerFactory.getLogger(BooksDao.class);
		
		try (Connection conn = DBUtils.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
	
			stmt.setLong(1, isbn);
			log.info(stmt.toString());
			ResultSet rs =  stmt.executeQuery();
			
			while(rs.next()) {
				reviews.add(resultSetToReviews(rs));
			}
			
			return reviews;
			
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return null;
		
	}
	
	private Review resultSetToReviews(ResultSet rs) {
		Review review = new Review();
		try {
			review.setReviewId(rs.getInt("reviewId"));
			review.setIsbn(rs.getLong("isbn"));
			review.setFullName(rs.getString("fullName"));
			review.setUserId(rs.getInt("userId"));
			review.setReview(rs.getString("review"));
			review.setRating(rs.getInt("rating"));
			System.out.println(rs.toString());
			return review;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public boolean addReview(Review review) {
		String sql = "INSERT INTO REVIEWS (issueId, userId, isbn, rating, review) VALUES (?, ?, ?, ?, ?)";
		Logger log = LoggerFactory.getLogger(ReviewsDao.class);
		try (Connection conn = DBUtils.createConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sql);) {		
			//stmt.setInt(1, review.getReviewId());
			if(review.getIssueId()!=null)
				stmt.setInt(1, review.getIssueId());
			else
				stmt.setInt(1, 202101);//default value for issueId
			
			stmt.setInt(2, review.getUserId());
			stmt.setLong(3, review.getIsbn());
			stmt.setInt(4, review.getRating());
			stmt.setString(5, review.getReview());

			log.info(stmt.toString());
			
			stmt.executeUpdate();
			return true;
		} 
		catch (Exception ex) {
			return false;
			
		}
	}
	
	public boolean updateReview(Review review) {
		String sql = "UPDATE reviews SET rating = ?, review = ? WHERE isbn = ? AND reviewId = ?";
		Logger log = LoggerFactory.getLogger(ReviewsDao.class);
		try (Connection conn = DBUtils.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {		
			stmt.setInt(1, review.getRating());
			stmt.setString(2, review.getReview());
			stmt.setLong(3, review.getIsbn());
			stmt.setInt(4, review.getReviewId());

			log.info(stmt.toString());
			stmt.executeUpdate();  
			
			return true;
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public Object checkReviewStatus(Integer userId, Long isbn) {
		String sql = "Select r.reviewId " +
				"from reviews as r where r.userId = ? AND r.isbn = ?";
		Logger log = LoggerFactory.getLogger(BooksDao.class);
		
		try (Connection conn = DBUtils.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
	
			stmt.setInt(1, userId);
			stmt.setLong(2, isbn);
			log.info(stmt.toString());
			ResultSet rs =  stmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt("reviewId");
			}
			
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return false;
	}
}
