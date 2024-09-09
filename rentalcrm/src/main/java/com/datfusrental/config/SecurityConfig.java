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
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/doLogin","/testing").permitAll()
                .antMatchers("/userRegistration").hasAuthority("SUPERADMIN")
                .antMatchers("/getUserDetails").hasAuthority("ADMIN")
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

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.datfusrental.jwt.JwtAuthenticationFilter;
//import com.datfusrental.security.CustomUserDetailsService;
//import com.datfusrental.security.JwtUtil;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private CustomUserDetailsService userDetailsService;
//
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return new JwtAuthenticationFilter();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/doLogin", "/testing").permitAll()
//                .antMatchers("/userRegistration").hasAuthority("SUPERADMIN")
//                .antMatchers("/getUserDetails").hasAuthority("ADMIN")
//                .anyRequest().authenticated()
//                .and().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .maximumSessions(1);
//
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//    }
//}
