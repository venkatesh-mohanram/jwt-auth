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

import com.vasi.learning.model.v1.UserType;
import com.vasi.learning.persistence.dao.IUserTypeDao;


public class UserTypeDao implements IUserTypeDao, ApplicationContextAware {
	private Logger logger = LoggerFactory.getLogger(UserDao.class);
	private ApplicationContext context;
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		ApplicationContext context1 = new ClassPathXmlApplicationContext("Spring-Module.xml");
		
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		logger.info("Setting the DataSource : UserTypeDao");
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private final class UserTypeMapper implements RowMapper<UserType> {

		@Override
		public UserType mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserType userType = new UserType();
			userType.setId(rs.getInt("type_id"));
			userType.setName(rs.getString("type_name"));
			return userType;
		}
		
	}

	@Override
	public int create(UserType userType) {
		String query = "INSERT INTO vl_user_type (type_name) "				
				+ "VALUES (:name)";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", userType.getId())
				.addValue("name", userType.getName());
		int result = jdbcTemplate.update(query, paramSource);
		return result;
	}

	@Override
	public UserType read(int id) {
		String query = "SELECT * FROM vl_user_type WHERE type_id = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		List<UserType> userTypes = jdbcTemplate.query(query, paramSource, new UserTypeMapper());
		UserType userType = null;
		if (userTypes.size() == 1) {
			userType = userTypes.get(0);
		}		
		return userType;
	}
	
	@Override
	public UserType read(String type) {
		String query = "SELECT * FROM vl_user_type WHERE type_name = :type";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("type", type);
		List<UserType> userTypes = jdbcTemplate.query(query, paramSource, new UserTypeMapper());
		UserType userType = null;
		if (userTypes.size() == 1) {
			userType = userTypes.get(0);
		}		
		return userType;
	}

	@Override
	public int update(UserType userType) {
		String sql = "UPDATE vl_user_type "
				+ "SET type_name = :name "				
				+ "WHERE type_id = :id";				
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", userType.getId())
				.addValue("name", userType.getName());						
		int result = jdbcTemplate.update(sql, paramSource);
		return result;
	}

	@Override
	public int delete(int id) {
		String sql = "DELETE FROM vl_user_type WHERE type_id = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		int result = jdbcTemplate.update(sql, paramSource);
		return result;
	}

	@Override
	public List<UserType> list() {
		String query = "SELECT * FROM vl_user_type";
		SqlParameterSource paramSource = new MapSqlParameterSource();				
		List<UserType> userTypes = jdbcTemplate.query(query, paramSource, new UserTypeMapper());				
		return userTypes;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.context = context;		
	}

}
