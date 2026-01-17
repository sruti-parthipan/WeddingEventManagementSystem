
//package com.ey.security;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//	 private final JwtUtil jwtUtil;
//	    private final UserDetailsService userDetailsService;
//  
//	    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
//	        this.jwtUtil = jwtUtil;
//	        this.userDetailsService = userDetailsService;
//	    
//	    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // Get Authorization header
//        String header = request.getHeader("Authorization");
//
//        // Check if header exists and starts with Bearer
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Extract token
//        String token = header.substring(7);
//
//        // Validate token
//        if (!jwtUtil.validateToken(token)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Extract email from token
//        String email = jwtUtil.extractUsername(token);
//
//        // Load user details from DB
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//        // Create authentication object
//        UsernamePasswordAuthenticationToken auth =
//                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//        // Set authentication in SecurityContext
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        // Continue filter chain
//        filterChain.doFilter(request, response);
//    }
//}
//
//


package com.ey.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ey.config.MultiUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Validates JWT from Authorization header and sets Authentication in SecurityContext.
 * Uses MultiUserDetailsService to load Admin, Client, or Vendor by email.
 */
 public class JwtAuthorizationFilter extends OncePerRequestFilter {
@Autowired
    private  JwtUtil jwtUtil;
    private MultiUserDetailsService multiUserDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, MultiUserDetailsService multiUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.multiUserDetailsService = multiUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token
        String token = header.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract email from token
        String email = jwtUtil.extractUsername(token);

        // Load user details from MultiUserDetailsService
        UserDetails userDetails = multiUserDetailsService.loadUserByUsername(email);

        // Create authentication object
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}


