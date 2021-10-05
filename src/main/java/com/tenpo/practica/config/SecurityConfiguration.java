package com.tenpo.practica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.tenpo.practica.exceptions.CustomAccessDeniedHandler;
import com.tenpo.practica.exceptions.CustomHttp403ForbiddenEntryPoint;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Qualifier("userDetailsServiceImpl")
	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
	CustomAccessDeniedHandler authHandler;
	
	@Autowired
	CustomHttp403ForbiddenEntryPoint httpForbiden;
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
    
    @Override
	public void configure(HttpSecurity   http) throws Exception {

    	http.httpBasic().and()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST,"/signup/**").permitAll()
            .antMatchers(HttpMethod.GET,"/login/**").permitAll()
            .antMatchers(HttpMethod.GET,"/deny").permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .accessDeniedHandler(authHandler)  
            .and()
             .exceptionHandling().authenticationEntryPoint(httpForbiden)
            .and()
    		.csrf().disable()
    		.formLogin().disable()
    		.logout().disable();
    	
    }
    
    
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    	
    		
    }
    

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(daoAuthenticationProvider());
    }
    
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(this.userDetailsService());
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
    
    
}