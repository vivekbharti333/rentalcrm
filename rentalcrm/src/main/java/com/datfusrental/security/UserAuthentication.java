package com.datfusrental.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.datfusrental.object.request.LoginRequestObject;
import com.datfusrental.services.UserService;

//public class UserAuthentication implements UserDetailsService {
	public class UserAuthentication {
	
	@Autowired
	UserService userService;

//	@Override 
//	   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	     
//		LoginRequestObject userDetails = userService.doLogin(username);
//		if(userDetails != null) {
//			
//		}
//		return null; 
//	   } 
	

}
