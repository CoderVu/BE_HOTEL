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
                        // Only allow ROLE_USER vs ROLE_ADMIN to access add hotel
//                        .antMatchers("/api/v1/rooms/all-rooms").hasAnyRole("USER", "ADMIN", "SUPPERUSER")
//                        .antMatchers("/api/v1/hotel/add-hotel").hasAnyRole("USER", "ADMIN")
//                        .antMatchers("/api/v1/rooms/admin/**").hasAnyRole("ADMIN", "SUPPERUSER")
//                        .antMatchers("/api/v1/booking/**").hasAnyRole("USER", "ADMIN")
//
//                        .antMatchers("/api/v1/auth/user/login",
//                                "/api/v1/rooms/types",
//                                "/api/v1/rooms/all-rooms",
//                                "/api/v1/rooms/all-rooms/{hotelId}",
//                                "/api/v1/rooms/available-rooms",
//                                "/api/v1/rooms/room/{roomId}",
//                                "/api/v1/users/profile/**",
//                                "/api/v1/users/{email}",
//
//                                "/api/v1/rooms/room/{roomId}/reviews",
//                                "/api/v1/hotel/hotels/managed-by/**"

                        .antMatchers("/api/v1/**").permitAll() // Allow everyone to access
                        .anyRequest().authenticated());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }







}