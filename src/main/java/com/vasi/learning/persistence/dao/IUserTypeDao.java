package com.vasi.learning.persistence.dao;

import java.util.List;

import com.vasi.learning.model.v1.UserType;

public interface IUserTypeDao extends IGenericDao {
	public int create(UserType userType);
	public UserType read(int id);
	public UserType read(String type);
	public int update(UserType userType);
	public int delete(int id);
	
	public List<UserType> list();
}
