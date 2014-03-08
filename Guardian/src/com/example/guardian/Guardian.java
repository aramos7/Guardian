package com.example.guardian;

/**
 * Simple class to manage guardian information
 * 
 * @author Death (Armando Ramos)
 * @date Feb. 28, 2014
 */
public class Guardian {
	
	private String name;
	private String email;
	private String phoneNumber;
	
	public Guardian(String name, String email, String phoneNumber) {
		this.name = name;
		this.email = email;
		this. phoneNumber = phoneNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
}
