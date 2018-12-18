package com.vasi.learning.api.resources.logmessages;

public enum CategoryMessage implements ApplicationLogMessage{
	SERVER_ERROR(100, "Internal Server error"), 
	INCORRECT_INPUT(101,"Bad request, please check whetehr all the mandatory fields are provided"), 
	RES_NOT_FOUND(102,"Category not found for the given id"), 
	CREATE_FAILED(103,"Unable to create the category"), 
	INCORRECT_STATUS_TEXT(104,"Status search should contain either 'Published' or 'Unpublished'"), 
	GET_BY_COURSE_ID(105, "Getting by course Id"), 
	GET_BY_COURSE_NAME(106, "Getting by course name"),
	CATEGORY_CREATED_SUCCESSFULLY(107, "Course Category Created Successfully"),
	CATEGORY_CREATION_FAILED(108, "Unable to create the Course Category, please check your input"),	
	CATEGORY_UPDATED_SUCCESSFULLY(109, "Course Category Updated Successfully"),
	CATEGORY_UPDATION_FAILED(110, "Unable to update the Caourse Category"),
	CATEGORY_DELETED_SUCCESSFULLY(111, "Course Category Deleted Successfully"),
	CATEGORY_DELETION_FAILED(112, "Unable to delete the Category, please check whether this category is in use by any course");

	private static int BASE_MSG_CODE = 1000;
	private int code;
	private String message;

	CategoryMessage(int code, String message) {
			this.code = code;
			this.message = message;
		}
	
	@Override
	public int getCode() {
		return BASE_MSG_CODE + code;
	}

	@Override
	public String getPhrase() {
		return message;
	}

	@Override
	public String toString() {
		String erMessage = "[" + getCode() + "]" + " " + getPhrase();
		return erMessage;
	}
}
