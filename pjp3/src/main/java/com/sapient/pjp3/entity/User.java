package com.sapient.pjp3.entity;

import java.sql.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
	private int id;
	private String fullname;
	private Date created_at;
	private int total_borrowed_books;
	private int current_borrowed_books;
	private int fine;
	

}
