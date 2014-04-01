package com.example.guardian;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Maintains all the information about a session, whether it is valid or not, etc. 
 * 
 * @author Death (Armando Ramos)
 * @date Feb. 28, 2014
 */
public class SessionManager {
	
	public static SessionManager SESSION = null;
    private String sessionID;
	private String username;
	private String password;
	private String email;
	private String name;
	private boolean validated;
	private ArrayList<Guardian> guardians;
	private String address;
    private HttpContext httpContext;
	
	public SessionManager(String username, String password, boolean validated) {
		this.username = username;
		this.password = password;
		this.validated = validated;
        sessionID = "";
        httpContext = new BasicHttpContext();
	}

    public void setValidated(boolean input) {
        validated = input;
    }
	
	public boolean isItValidated() {
		return validated;
	}
	
	public void setGuardians(ArrayList<Guardian> guardians) {

        this.guardians = guardians;
	}
	
	public ArrayList<Guardian> getGuardians() {
		return guardians;
	}
	
	public void addMoreInfo(String email, String address, String name) {
		this.email = email;
		this.name = name;
		this.address = address;
	}

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public void setHttpContext(HttpContext httpContext){
        this.httpContext = httpContext;
    }

    public void setSessionID(String input) {
        sessionID = input;
    }

    public String getSessionId() {
        return sessionID;
    }
	
	
}
