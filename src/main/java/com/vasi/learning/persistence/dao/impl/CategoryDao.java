package com.vasi.learning.persistence.dao.impl;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.vasi.learning.model.v1.Category;
import com.vasi.learning.persistence.dao.ICategoryDao;

public class CategoryDao implements ICategoryDao, ApplicationContextAware {
	private ApplicationContext context;
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		ApplicationContext context1 = new ClassPathXmlApplicationContext("Spring-Module.xml");
		CategoryDao caregoryDao = (CategoryDao) context1.getBean("categoryDao");
		// Create Category object
		Category category = new Category();
		category.setId(1);
		category.setName("Athichudi");
		category.setRoot(true);
		int result = caregoryDao.update(category);
		System.out.println(result);
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		System.out.println("Setting the DataSource : CategoryDao");
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private final class CategoryMapper implements RowMapper<Category> {

		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			Category category = new Category();
			category.setId(rs.getInt("category_id"));
			category.setName(rs.getString("category_name"));
			Blob image = rs.getBlob("category_image");
			if (image != null) {
				byte[] imageBytes = image.getBytes(0, (int)image.length());			
				image.free();
				category.setImage(imageBytes);
			}
			category.setRoot(rs.getBoolean("category_root"));
			// Get all the child categories
			CategoryDao categoryDao = context.getBean(CategoryDao.class);
			List<Category> children = categoryDao.listChildren(category.getId());
			category.setChildren(children);
			return category;
		}
		
	}
	
	@Override
	public int create(Category category) {
		String query = "INSERT INTO vl_category (category_name, category_image, category_root, category_parent) "				
				+ "VALUES (:name, :image, :root,:parent)";
		SqlParameterSource paramSource = new MapSqlParameterSource()				
				.addValue("name", category.getName())
				.addValue("image", category.getImage())
				.addValue("root", category.getRoot())
				.addValue("parent", category.getParent()!=null?category.getParent().getId():null);
		int result = jdbcTemplate.update(query, paramSource);
		return result;
	}

	@Override
	public Category read(int id) {
		String query = "SELECT * FROM vl_category WHERE category_id = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		List<Category> categories = jdbcTemplate.query(query, paramSource, new CategoryMapper());
		Category selectedCategory = null;
		if (categories.size() == 1) {
			selectedCategory = categories.get(0);
		}		
		return selectedCategory;
	}

	@Override
	public int update(Category category) {
		String sql = "UPDATE vl_category "
				+ "SET category_name = :name, category_image = :image, category_root = :root, category_parent = :parent "				
				+ "WHERE category_id = :id";				
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", category.getId())
				.addValue("name", category.getName())
				.addValue("image", category.getImage())
				.addValue("root", category.getRoot())
				.addValue("parent", category.getParent()!=null?category.getParent().getId():null);		
		int result = jdbcTemplate.update(sql, paramSource);
		return result;
	}

	@Override
	public int delete(int id) {
		String sql = "DELETE FROM vl_category WHERE category_id = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		int result = jdbcTemplate.update(sql, paramSource);
		return result;
	}

	@Override
	public List<Category> list() {
		String query = "SELECT * FROM vl_category";
		SqlParameterSource paramSource = new MapSqlParameterSource();				
		List<Category> categories = jdbcTemplate.query(query, paramSource, new CategoryMapper());			
		return categories;
	}

	@Override
	public List<Category> listRootCategories() {
		String query = "SELECT * FROM vl_category WHERE category_root = 1";
		SqlParameterSource paramSource = new MapSqlParameterSource();				
		List<Category> categories = jdbcTemplate.query(query, paramSource, new CategoryMapper());			
		return categories;
	}

	@Override
	public List<Category> listChildren(int id) {
		String query = "SELECT * FROM vl_category WHERE category_parent = :id";
		SqlParameterSource paramSource = new MapSqlParameterSource()
				.addValue("id", id);
		List<Category> categories = jdbcTemplate.query(query, paramSource, new CategoryMapper());				
		return categories;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
		
	}

}
