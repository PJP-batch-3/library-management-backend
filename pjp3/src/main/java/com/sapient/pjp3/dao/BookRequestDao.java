package com.sapient.pjp3.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sapient.pjp3.utils.DBUtils;

public class BookRequestDao {
	public static Boolean create(Integer USER_ID,String BOOK_TITLE,String AUTHOR,Date date) {
		String sql = "INSERT INTO BOOK_REQUESTS(USER_ID, BOOK_TITLE, AUTHOR) values ( ?, ?, ?)";
		Logger log = LoggerFactory.getLogger(BookRequestDao.class);
		try (Connection conn = DBUtils.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
			
			stmt.setInt(1, USER_ID);
			stmt.setString(2, BOOK_TITLE);
			stmt.setString(3, AUTHOR);
			log.info(conn.toString());
			log.info(stmt.toString());
			stmt.execute();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}