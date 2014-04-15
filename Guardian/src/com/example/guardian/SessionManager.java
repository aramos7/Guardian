package com.example.guardian;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceActivity;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Maintains all the information about a session, whether it is valid or not, etc. 
 * 
 * @author Death (Armando Ramos)
 * @date Feb. 28, 2014
 */
public class SessionManager extends Observable implements Serializable{
	
	public static SessionManager SESSION = null;
    private String sessionID;
	private String username;
	private String password;
	private String email;
	private String name;
	private boolean validated = false;
	private ArrayList<Guardian> guardians;
	private String address;
    private HttpContext httpContext;
    private long endDate;
    private long startDate;
    private ArrayList<Location> locationsArray;
    private final static String SAVE_FILE_NAME = "session-guardian";
    private BasicCookieStore cookieStore;
	
	public SessionManager(String email, String password, boolean validated) {
		this.email = email;
		this.password = password;
		this.validated = validated;
        sessionID = "";
        httpContext = new BasicHttpContext();
        guardians = new ArrayList<Guardian>();
        locationsArray = new ArrayList<Location>();
	}

    public void setValidated(boolean input) {
        validated = input;
    }
	
	public boolean getValidated() {
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

    public void setCookieStore (BasicCookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public BasicCookieStore getCookieStore () {
        return cookieStore;
    }

    public String getSessionId() {
        return sessionID;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void updateLocationsArray(Location location) {
        locationsArray.add(location);
    }

    public void save(Context context) {
        this.setChanged();
        this.notifyObservers();
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput(SAVE_FILE_NAME, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            fileOut.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static SessionManager load(Context context) {
        ObjectInputStream objectIn = null;
        Object object = null;
        try {
            FileInputStream fileIn = context.getApplicationContext().openFileInput(SAVE_FILE_NAME);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do now
                }
            }
        }

        return (SessionManager)object;
    }

    public static void clearSession(Context context) {
        context.getApplicationContext().deleteFile(SAVE_FILE_NAME);
        SESSION = null;
    }

    public ArrayList<Location> getLocationsArray() {
        return locationsArray;
    }
}
