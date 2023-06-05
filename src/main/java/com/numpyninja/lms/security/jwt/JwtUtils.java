package com.numpyninja.lms.security.jwt;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import static com.numpyninja.lms.util.LMSConstants.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.app.jwtSecret}")
    private String jwtSecret;

    @Value("${security.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${security.app.jwtAcctActiveExpMs}")
    private int jwtAcctActiveExpMs;
    
    @Value("${security.app.jwtForgotPasswordExpMs}")
    private int jwtForgotPasswordExpMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                //.claim( ROLES , authorities)   // uncomment if u want to include roles in token
                .setIssuedAt(new Date())
                .setExpiration(new Date( (new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String generateEmailUrlToken(String loginEmail){
       return  Jwts.builder()
                .setSubject((loginEmail))
                //.claim( ROLES , authorities)   // uncomment if u want to include roles in token
                .setIssuedAt(new Date())
                .setExpiration(new Date( (new Date()).getTime() + jwtAcctActiveExpMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String validateAccountActivationToken(String token) {
        String validity = "Invalid";
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token); // If the token is not valid, it will throw exception
            validity = "Valid";  // If the token is valid only, control will come here; else it will land in one of catch block
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("MalformedJwtException: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("SignatureException", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException", e.getMessage());
        }
        return validity;
    }
    
    public String generateJwtTokenForgotPwd(String emailId){
    	return Jwts.builder()
    			.setSubject(emailId)
    			.setIssuedAt(new Date())
    			.setExpiration(new Date((new Date()).getTime() + jwtForgotPasswordExpMs))
    			.signWith(SignatureAlgorithm.HS512, jwtSecret)
    			.compact();
    }


}