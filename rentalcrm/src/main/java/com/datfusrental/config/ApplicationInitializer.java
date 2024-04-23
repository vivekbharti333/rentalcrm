package com.datfusrental.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
	 private static final Logger logger = LogManager.getLogger(ApplicationInitializer.class);

	@Override
	protected Class<?>[] getRootConfigClasses() {
		logger.info("Enter into Intilizer");
		return new Class[] {ApplicationConfiguration.class}; 
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] {ApplicationConfiguration.class}; 
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}

}
