package com.datfusrental.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.datfusrental.entities.User;
import com.datfusrental.helper.UserHelper;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	 @Autowired
	 private UserHelper userHelper;

//    @Autowired
//    private LoginRequestObject userRepository; // Assume you have a UserRepository

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	User user = userHelper.getUserDetailsByLoginId(username); // Load user from your data source
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getLoginId(), user.getPassword(), new ArrayList<>());
    }
}