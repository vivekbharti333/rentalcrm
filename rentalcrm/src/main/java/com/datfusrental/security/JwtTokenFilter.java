package com.datfusrental.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.datfusrental.jwt.JwtTokenUtil;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	// Simple JWT implementation
	@Autowired
	private JwtTokenUtil jwtUtil;


	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {

		// trying to find Authorization header
		final String authorizationHeader = httpServletRequest.getHeader("Authorization");
		if (authorizationHeader == null || authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Bearer")) {
			// if Authorization header does not exist,
			// then skip this filter
			// and continue to execute next filter class
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}
	}

}