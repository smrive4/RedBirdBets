package com.predictionmarket.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Filter that intercepts incoming HTTP requests to validate JWT tokens and set the security context for authenticated users
@Component
public class JwtFilter extends OncePerRequestFilter{
    @Autowired
    private JwtUtil jwtUtil;

    /* Filter that intercepts incoming HTTP requests to validate JWT tokens and set the security context for authenticated users */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
    
        if (!jwtUtil.validateToken(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UsernamePasswordAuthenticationToken result  = UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(result);

        filterChain.doFilter(request, response);
    }

}