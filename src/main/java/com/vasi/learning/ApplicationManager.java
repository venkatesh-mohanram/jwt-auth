package com.vasi.learning;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationManager {
	private static ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
	
	public static ApplicationContext getApplicationContext() {
		return context;
	}
}
