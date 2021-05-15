package com.sapient.pjp3.entity;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Review {
	private int review_id;
	private int issue_id;
	private int user_id;
	private long isbn;
	private int rating;
	private String review;

	public int getReview_id() {return review_id; }
	public void setReview_id(int id) { this.review_id = id; }

	public int getIssue_id() { return issue_id; }
	public void setIssue_id(int issue_id) { this.issue_id = issue_id; }

	public int getUser_id() { return user_id; }
	public void setUser_id(int user_id) { this.user_id = user_id; }

	public long getIsbn() { return isbn; }
	public void setIsbn(long isbn) { this.isbn = isbn; }

	public int getRating() { return rating; }
	public void setRating(int rating) { this.rating = rating; }

	public String getReview() { return review; }
	public void setReview(String review) { this.review = review; }

	
}
