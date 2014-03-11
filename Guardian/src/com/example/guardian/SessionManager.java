package com.example.guardian;

/**
 * Maintains all the information about a session, whether it is valid or not, etc. 
 * 
 * @author Death (Armando Ramos)
 * @date Feb. 28, 2014
 */
public class SessionManager {
	
	public static SessionManager SESSION = null;
	private String username;
	private String password;
	private String email;
	private String name;
	private boolean validated;
	private Guardian[] guardians;
	private String address;
	
	public SessionManager(String username, String password, boolean validated) {
		this.username = username;
		this.password = password;
		this.validated = validated;
	}

    public void setValidated(boolean input) {
        validated = input;
    }
	
	public boolean isItValidated() {
		return validated;
	}
	
	public void setGuardians(Guardian[] guardians) {
		this.guardians = guardians;
	}
	
	public Guardian[] getGuardians() {
		return guardians;
	}
	
	public void addMoreInfo(String email, String address, String name) {
		this.email = email;
		this.name = name;
		this.address = address;
	}
	
	
}
