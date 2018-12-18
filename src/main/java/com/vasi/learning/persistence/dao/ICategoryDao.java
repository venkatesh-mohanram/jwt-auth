package com.vasi.learning.persistence.dao;

import java.util.List;

import com.vasi.learning.model.v1.Category;

public interface ICategoryDao extends IGenericDao{
	public int create(Category category);
	public Category read(int id);
	public int update(Category category);
	public int delete(int id);
	
	public List<Category> list();
	public List<Category> listRootCategories();
	public List<Category> listChildren(int id);
}
