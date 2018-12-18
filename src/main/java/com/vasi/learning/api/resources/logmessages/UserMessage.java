package com.vasi.learning.api.resources.logmessages;

public enum UserMessage implements ApplicationLogMessage{
	SERVER_ERROR(100, "Internal Server error"), 
	INCORRECT_INPUT(101,"Bad request, please check whether all the mandatory fields are provided"), 
	RES_NOT_FOUND(102,"user not found for the given id"), 
	CREATE_FAILED(103,"Unable to create the user"),
	USER_CREATED_SUCCESSFULLY(107, "User Created Successfully"),
	USER_CREATION_FAILED(108, "Unable to create the user, please check your input"),	
	USER_UPDATED_SUCCESSFULLY(109, "User Updated Successfully"),
	USER_UPDATION_FAILED(110, "Unable to update the user"),
	USER_DELETED_SUCCESSFULLY(111, "User Deleted Successfully"),
	USER_DELETION_FAILED(112, "Unable to delete the user");

	private static int BASE_MSG_CODE = 2000;
	private int code;
	private String message;

	UserMessage(int code, String message) {
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
