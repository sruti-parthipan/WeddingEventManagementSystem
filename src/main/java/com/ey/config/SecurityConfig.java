//package com.ey.config;
//
//
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
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
//@Configuration
//@EnableWebSecurity
//
//public class SecurityConfig {
//
//    private final JwtUtil jwtUtil;
//    private final MultiUserDetailsService multiUserDetailsService;
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
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
//        jwtAuthFilter.setFilterProcessesUrl("/api/auth/login");
//
//        http.csrf(csrf -> csrf.disable())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/auth/**").permitAll()
//                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                .requestMatchers("/api/client/**").hasAnyRole("CLIENT", "ADMIN")
//                .requestMatchers("/api/vendor/**").hasAnyRole("VENDOR", "ADMIN")
//                .anyRequest().authenticated()
//            )
//            .addFilter(jwtAuthFilter)
//            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, multiUserDetailsService), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}

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
 private  JwtUtil jwtUtil;
@Autowired
 private  MultiUserDetailsService multiUserDetailsService;
    @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authManager, jwtUtil);
        jwtAuthFilter.setFilterProcessesUrl("/api/auth/login"); // Common login endpoint
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            		
                .requestMatchers("/api/auth/**").permitAll() // Registration + Login
                .requestMatchers("/api/admin/register").permitAll()
                .requestMatchers("/api/client/register").permitAll()
                .requestMatchers("/api/vendor/register").permitAll()
.requestMatchers("/api/forgot-password").permitAll()
.requestMatchers("/api/reset-token").permitAll()

//2) Allow BOTH CLIENT and ADMIN to view vendors exposed under /api/client/vendors/**
         //    (Must be BEFORE the generic /api/client/** rule)
         .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/client/vendors/**")
             .hasAnyRole("CLIENT", "ADMIN")
             .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/client/events/**")
             .hasAnyRole("CLIENT", "ADMIN")
.requestMatchers(HttpMethod.GET,  "/api/vendor/bookings").hasRole("VENDOR")
.requestMatchers(HttpMethod.PUT,
    "/api/vendor/bookings/*/accept",
    "/api/vendor/bookings/accept/*",
    "/api/vendor/bookings/*/cancel",
    "/api/vendor/bookings/cancel/*"
).hasRole("VENDOR")

.requestMatchers(HttpMethod.POST, "/api/vendor/bookings/*/confirm-payment").hasRole("VENDOR")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                //some operation of client can be done by only client like create event,book
                .requestMatchers("/api/client/**").hasRole("CLIENT")
                //SOME PERATION CAN BE DONE BY VENDOR ONLY LIKE UPDATE BOOKING,CANCEL..
                .requestMatchers("/api/vendor/**").hasRole("VENDOR")
                //SOME PERATIONS IN CLIENT LIKE VIEWING THE CLIENT'S GET METHODS ONLY...
                .requestMatchers("/api/clients/**").hasAnyRole("CLIENT", "ADMIN")
                .requestMatchers("/api/vendors/**").hasAnyRole("CLIENT","VENDOR", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilter(jwtAuthFilter)
            .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, multiUserDetailsService), UsernamePasswordAuthenticationFilter.class);
       return http.build();
    }
}







//package com.ey.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
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
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtUtil jwtUtil;
//    private final MultiUserDetailsService multiUserDetailsService;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // Spring Security 6+ way to get AuthenticationManager
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    // Use your MultiUserDetailsService for DAO auth
//    @Bean
//    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
//                                                               PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
//        provider.setPasswordEncoder(passwordEncoder);
//        return provider;
//    }
//
//    // Expose it as UserDetailsService bean (or annotate MultiUserDetailsService with @Service)
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return multiUserDetailsService;
//    }
//
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authManager) {
//        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authManager, jwtUtil);
//        filter.setFilterProcessesUrl("/api/auth/login"); // common login endpoint
//        return filter;
//    }
//
//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilter() {
//        return new JwtAuthorizationFilter(jwtUtil, multiUserDetailsService);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http,
//                                                   DaoAuthenticationProvider daoAuthProvider,
//                                                   JwtAuthenticationFilter jwtAuthFilter,
//                                                   JwtAuthorizationFilter jwtAuthorizationFilter) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authenticationProvider(daoAuthProvider)
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/auth/**").permitAll()          // registration + login
//                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                .requestMatchers("/api/client/**").hasAnyRole("CLIENT", "ADMIN")
//                .requestMatchers("/api/vendor/**").hasAnyRole("VENDOR", "ADMIN")
//                .anyRequest().authenticated()
//            )
//            // Authorization must run BEFORE UsernamePasswordAuthenticationFilter
//            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
//            // Authentication (login) filter for /api/auth/login
//            .addFilter(jwtAuthFilter);
//
//        return http.build();
//    }
//}
//
