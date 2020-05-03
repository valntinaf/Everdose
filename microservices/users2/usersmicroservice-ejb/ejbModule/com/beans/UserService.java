package com.beans;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.entities.User;
import com.classes.Token;

/**
 * Session Bean implementation class UserService
 */
@Stateless
@LocalBean
public class UserService {
	static final long ONE_MINUTE_IN_MILLIS=60000;
    /**
     * Default constructor. 
     */
    public UserService() {
        // TODO Auto-generated constructor stub
    }
    
    public Token getToken(String username,String password) {
    	Token token=null;
    	User user=User.getUser(username);
    	if(user.getPassword().equals(password)) {
    		Calendar date = Calendar.getInstance();
    		long t= date.getTimeInMillis();
    		Date expirationTime=new Date(t + (10 * ONE_MINUTE_IN_MILLIS));
    		token=new Token(username,expirationTime);
    	}
    	return token;
    }
    
    public boolean checkToken(Token token) {
    	return token.isValid();
    }
    
    public boolean createUser(String username, String email,String name,String password) {
    	User user=new User();
    	user.setEmail(email);
    	user.setUsername(username);
    	user.setPassword(password);
    	user.setName(name);
    	return user.save();
    }

	public boolean updateUser(String username, String email, String name, String newPassword) {
		User user=User.getUser(username);
		if(email!=null) {user.setEmail(email);}
		if(name!=null) {user.setName(name);}
		if(newPassword!=null) {user.setPassword(newPassword);}
		return user.save();
	}

	public boolean deleteUser(String username) {
		return User.deleteByUsername(username);
	}
    
}
