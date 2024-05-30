package com.datfusrental.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.datfusrental.security.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtTokenFilter jwtTokenFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// disabling csrf since we won't use form login
				.csrf().disable()

				// giving every permission to every request for /login endpoint
				.authorizeRequests()
				.antMatchers("/doLogin").permitAll()
                .antMatchers("/userRegistration").hasAuthority("SUPERADMIN")
                .antMatchers("/suspectList").hasAuthority("ADMIN")
				.anyRequest().authenticated()
				// setting stateless session, because we choose to implement Rest API
				.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.maximumSessions(1);

		// adding the custom filter before UsernamePasswordAuthenticationFilter in the
		// filter chain
		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
