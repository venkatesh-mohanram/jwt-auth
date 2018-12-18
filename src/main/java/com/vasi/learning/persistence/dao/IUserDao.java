package com.vasi.learning.persistence.dao;

import java.util.List;

import com.vasi.learning.model.v1.User;

public interface IUserDao extends IGenericDao {
	public int create(User user);
	public User read(int id);
	public User read(String email);
	public int update(User user);
	public int delete(int id);
	
	public List<User> list();
	public List<User> list(int userType);
	
	public boolean isAuthentic(String username, String password);
}
