package com.example.booking_hotel.security;

import com.example.booking_hotel.security.jwt.AuthTokenFilter;
import com.example.booking_hotel.security.jwt.JwtAuthEntryPoint;
import com.example.booking_hotel.security.user.HotelUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig {
    private final HotelUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public AuthTokenFilter authenticationTokenFilter(){
        return new AuthTokenFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(auth -> auth
                        .antMatchers("/api/v1/users/all_roles").permitAll()// Only allow ADMIN to access all bookings
                        .antMatchers("/api/v1/bookings/all-bookings").permitAll() // Only allow ADMIN to delete user
                        .antMatchers("/api/v1/bookings/confirmation/{confirmationCode}").permitAll()
                        .antMatchers("/api/v1/bookings/user/{userId}/bookings").permitAll()
                        .antMatchers("/api/v1/auth/update-user/{userId}").permitAll()
                        .antMatchers("/api/v1/ratings/rate/**").permitAll()// Only allow ADMIN and USER to rate room

                        .antMatchers("/api/v1/rooms/add/new-room").permitAll() // Only allow ADMIN to add new room
                        .antMatchers("/api/v1/rooms/update/{roomId}").permitAll() // Only allow ADMIN to update room
                        .antMatchers("/api/v1/rooms/room/{roomId}").permitAll() // Only allow ADMIN to view room
                        .antMatchers("/api/v1/rooms/delete/room/{roomId}").permitAll() // Only allow ADMIN to delete room
                        .antMatchers("/api/v1/auth/**", "/api/v1/rooms/types", "/api/v1/rooms/all-rooms", "/api/v1/rooms/available-rooms","/api/v1/rooms/room/{roomId}",
                                "/api/v1/users/profile/**","/api/v1/users/{email}","/api/v1/bookings/**","/api/v1/rooms/room/{roomId}/reviews").permitAll() // Allow everyone to access
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }







}