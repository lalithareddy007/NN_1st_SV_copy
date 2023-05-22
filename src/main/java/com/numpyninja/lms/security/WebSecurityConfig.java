package com.numpyninja.lms.security;

import com.numpyninja.lms.cache.UserDetailsCache;
import com.numpyninja.lms.security.jwt.AuthEntryPointJwt;
import com.numpyninja.lms.security.jwt.AuthTokenFilter;
import com.numpyninja.lms.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity   // allows Spring to find and automatically apply the class to the global Web Security.
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        jsr250Enabled = true,    // enables @RolesAllowed annotation.
        prePostEnabled = true )  // provides AOP security on methods. It enables @PreAuthorize, @PostAuthorize
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserServices userServices;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userServices).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserCache userCache() {
        return new UserDetailsCache();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()   // CORS is enabled , CSRF is disabled
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/login").permitAll()
                .anyRequest().authenticated();        // ”permitAll” will configure the authorization so that all requests are allowed on that particular path ; '/login'

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

//  AuthenticationEntryPoint : a filter which is the first point of entry for Spring Security.
//  It is the entry point to check if a user is authenticated and logs the person in or throws exception (unauthorized).
//  Usually the class can be used like that in simple applications but when using Spring security in REST, JWT etc
//  one will have to extend it to provide better Spring Security filter chain management.