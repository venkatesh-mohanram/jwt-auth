package com.vasi.learning.model.v1;

public class ResponseMessage {
	private int status;
	private int code;
	private String message;
	private String description;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int errorCode) {
		this.code = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String errorMessage) {
		this.message = errorMessage;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String exceptionMessage) {
		this.description = exceptionMessage;
	}
}
