package com.vasi.learning.persistence.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.vasi.learning.model.v1.User;
import com.vasi.learning.model.v1.UserType;
import com.vasi.learning.persistence.dao.IUserDao;
import com.vasi.learning.persistence.util.AuthenticationManagerUtils;

public class UserDao implements IUserDao, ApplicationContextAware {
	private Logger logger = LoggerFactory.getLogger(UserDao.class);
	private ApplicationContext context;
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		ApplicationContext context1 = new ClassPathXmlApplicationContext("Spring-Module.xml");
		
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		logger.info("Setting the DataSource : UserDao");
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private final class UserMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt("user_id"));
			user.setFirstName(rs.getString("user_firstname"));
			user.setLastName(rs.getString("user_lastname"));
			user.setEmail(rs.getString("user_email"));
			user.setDob(rs.getDate("user_dob"));
			user.setMobile(rs.getString("user_mobile"));
			user.setCountry(rs.getString("user_country"));
			// Get the user_type name
			UserTypeDao userTypeDao = context.getBean(UserTypeDao.class);
			UserType userType = userTypeDao.read(rs.getInt("user_type"));
			user.setUserType(userType);
			return user;
		}
		
	}
	@Override
	public int create(User user) {
		String query = "INSERT INTO vl_user (user_firstname, user_lastname, user_email, user_password, user_dob, user_mobile, user_country, user_type) "				
				+ "VALUES (:fname, :lname, :email, :password, :dob, :mobile, :country, :type)";
		String password = AuthenticationManagerUtils.generateRandomPassword();
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("fname", user.getFirstName())
				.addValue("lname", user.getLastName())
				.addValue("email", user.getEmail())
				.addValue("password", AuthenticationManagerUtils.hashPassword(password))
				.addValue("dob", user.getDob())
				.addValue("mobile", user.getMobile())
				.addValue("country", user.getCountry())
				.addValue("type", user.getUserType().getId());
		int result = jdbcTemplate.update(query, paramSource);
		if (result == 1) {
			AuthenticationManagerUtils.sendEmail(user, password);
		}
		return result;
	}

	@Override
	public User read(int id) {
		String query = "SELECT * FROM vl_user WHERE user_id = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		List<User> users = jdbcTemplate.query(query, paramSource, new UserMapper());
		User user = null;
		if (users.size() == 1) {
			user = users.get(0);
		}		
		return user;
	}
	
	@Override
	public User read(String email) {
		String query = "SELECT * FROM vl_user WHERE user_email = :email";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("email", email);
		List<User> users = jdbcTemplate.query(query, paramSource, new UserMapper());
		User user = null;
		if (users.size() == 1) {
			user = users.get(0);
		}		
		return user;
	}

	@Override
	public int update(User user) {
		String sql = "UPDATE vl_user "
				+ "SET user_firstname = :fname, user_lastname = :lname, user_dob = :dob, user_mobile = :mobile, user_country = :country "				
				+ "WHERE user_id = :id";				
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", user.getId())
				.addValue("fname", user.getFirstName())
				.addValue("lname", user.getLastName())				
				.addValue("dob", user.getDob())
				.addValue("mobile", user.getMobile())
				.addValue("country", user.getCountry());						
		int result = jdbcTemplate.update(sql, paramSource);
		return result;
	}

	@Override
	public int delete(int id) {
		String sql = "DELETE FROM vl_user WHERE user_id = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		int result = jdbcTemplate.update(sql, paramSource);
		return result;
	}

	@Override
	public List<User> list() {
		String query = "SELECT * FROM vl_user";
		SqlParameterSource paramSource = new MapSqlParameterSource();				
		List<User> users = jdbcTemplate.query(query, paramSource, new UserMapper());				
		return users;
	}

	@Override
	public List<User> list(int userType) {
		String query = "SELECT * FROM vl_user WHERE user_type = :userType";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("userType", userType);				
		List<User> users = jdbcTemplate.query(query, paramSource, new UserMapper());				
		return users;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	@Override
	public boolean isAuthentic(String username, String password) {
		System.out.println("UserDao.isAuthentic");
		boolean isAuthentic = false;
		String query = "SELECT user_password FROM vl_user WHERE user_email = :email";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("email", username);				
		String storedPassword = jdbcTemplate.queryForObject(query, paramSource, String.class);
		System.out.println("Stored Pwd: " + storedPassword + " and given pwd is " + password);
		if (storedPassword != null && password != null) {			
			isAuthentic = storedPassword.equals(password);
		}
		return isAuthentic;
	}

}
