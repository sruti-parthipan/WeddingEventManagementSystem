//
//
//
//package com.ey.config;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.ey.security.JwtAuthenticationFilter;
//import com.ey.security.JwtAuthorizationFilter;
//import com.ey.security.JwtUtil;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//@Autowired
// private  JwtUtil jwtUtil;
//@Autowired
// private  MultiUserDetailsService multiUserDetailsService;
//    @Bean
//   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
//        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authManager, jwtUtil);
//        jwtAuthFilter.setFilterProcessesUrl("/api/auth/login"); // Common login endpoint
//        http.csrf(csrf -> csrf.disable())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//            		
//                .requestMatchers("/api/auth/**").permitAll() // Registration + Login
//                .requestMatchers("/api/admin/register").permitAll()
//                .requestMatchers("/api/client/register").permitAll()   
//                .requestMatchers("/api/vendor/register").permitAll()
//.requestMatchers("/api/forgot-password").permitAll()
//.requestMatchers("/api/reset-token").permitAll()
//
////2) Allow BOTH CLIENT and ADMIN to view vendors exposed under /api/client/vendors/**
//         //    (Must be BEFORE the generic /api/client/** rule)
//         .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/client/vendors/**")
//             .hasAnyRole("CLIENT", "ADMIN")
//             .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/client/events/**")
//             .hasAnyRole("CLIENT", "ADMIN")
//             
//             .requestMatchers(HttpMethod.GET,  "/api/client/events/**").hasAnyRole("ADMIN")
//             .requestMatchers(HttpMethod.POST, "/api/client/reviews/**").hasRole("CLIENT")
//.requestMatchers(HttpMethod.GET,  "/api/vendor/bookings").hasRole("VENDOR")
//.requestMatchers(HttpMethod.PUT,
//    "/api/vendor/bookings/*/accept",
//    "/api/vendor/bookings/accept/*",
//    "/api/vendor/bookings/*/cancel",
//    "/api/vendor/bookings/cancel/*"
//).hasRole("VENDOR")
//
//
//.requestMatchers(HttpMethod.POST, "/api/vendor/bookings/*/confirm-payment").hasRole("VENDOR")
//                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                //some operation of client can be done by only client like create event,book
//                .requestMatchers("/api/client/**").hasRole("CLIENT")
//                //SOME PERATION CAN BE DONE BY VENDOR ONLY LIKE UPDATE BOOKING,CANCEL..
//                .requestMatchers("/api/vendor/**").hasRole("VENDOR")
//                //SOME PERATIONS IN CLIENT LIKE VIEWING THE CLIENT'S GET METHODS ONLY...
//                .requestMatchers("/api/clients/**").hasAnyRole("CLIENT", "ADMIN")
//                .requestMatchers("/api/vendors/**").hasAnyRole("CLIENT","VENDOR", "ADMIN")
//                .anyRequest().authenticated()
//            )
//            .addFilter(jwtAuthFilter)
//            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, multiUserDetailsService), UsernamePasswordAuthenticationFilter.class);
//       return http.build();
//    }
//}
//





package com.ey.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ey.security.JwtAuthenticationFilter;
import com.ey.security.JwtAuthorizationFilter;
import com.ey.security.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MultiUserDetailsService multiUserDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    	return config.getAuthenticationManager();
           }
  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authManager, jwtUtil);
        jwtAuthFilter.setFilterProcessesUrl("/api/auth/login");

        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ---------------- PUBLIC ----------------
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/register").permitAll()
                .requestMatchers("/api/client/register").permitAll()
                .requestMatchers("/api/vendor/register").permitAll()
                .requestMatchers("/api/forgot-password").permitAll()
                .requestMatchers("/api/reset-token").permitAll()

                // ---------------- CLIENT REVIEW POST (must be early) ----------------
                .requestMatchers(HttpMethod.POST, "/api/client/reviews/**").hasRole("CLIENT")

                // ---------------- CLIENT GET views (Admin & Client can view vendor/event lists) ----------------
                .requestMatchers(HttpMethod.GET, "/api/client/vendors/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/client/events/**").hasAnyRole("CLIENT", "ADMIN")

                // ---------------- VENDOR BOOKING APIs ----------------
                .requestMatchers(HttpMethod.GET, "/api/vendor/bookings").hasRole("VENDOR")
                .requestMatchers(HttpMethod.PUT,
                        "/api/vendor/bookings/*/accept",
                        "/api/vendor/bookings/accept/*",
                        "/api/vendor/bookings/*/cancel",
                        "/api/vendor/bookings/cancel/*"
                ).hasRole("VENDOR")

                .requestMatchers(HttpMethod.POST, "/api/vendor/bookings/*/confirm-payment").hasRole("VENDOR")

                // ---------------- ADMIN FULL ACCESS ----------------
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // ---------------- CLIENT FULL ACCESS ----------------
                .requestMatchers("/api/client/**").hasRole("CLIENT")

                // ---------------- VENDOR FULL ACCESS ----------------
                .requestMatchers("/api/vendor/**").hasRole("VENDOR")

                // ---------------- CROSS-ROLE VIEW ENDPOINTS ----------------
                .requestMatchers("/api/clients/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers("/api/vendors/**").hasAnyRole("CLIENT", "VENDOR", "ADMIN")

                // ---------------- ANY OTHER REQUIRES AUTH ----------------
                .anyRequest().authenticated()
            )
            .addFilter(jwtAuthFilter)
            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, multiUserDetailsService),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



