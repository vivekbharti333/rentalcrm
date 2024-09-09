package com.datfusrental.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@CrossOrigin(origins = "*")
@RestController
public class Testing {

	@Autowired
	public HttpServletRequest request;

	@RequestMapping(value = "/log")
	public ModelAndView test(HttpServletResponse response) throws IOException {
		return new ModelAndView("home");
	}

	@RequestMapping(value = "/testing")
	public String testing(HttpServletResponse response) throws IOException {
		return "Security Working";
	}
	
	@RequestMapping(value = "/")
	public String version(HttpServletResponse response) throws Exception {

		return "1.2";
	}

}
