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
                .cors().and()
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(auth -> auth
                        .antMatchers("/api/v1/auth/user/update-user/{userId}").hasAnyRole("USER", "ADMIN", "SUPPERUSER")
                        .antMatchers("/api/v1/booking/all-booking","/api/v1/booking/all-bookingOfOneHotel/{adminId}","/api/v1/booking/history-booking/email/{email}","/api/v1/booking/room/{roomId}/booking","/api/v1/booking/booking/{bookingId}/delete").hasAnyRole("USER", "ADMIN", "SUPPERUSER")
                        .antMatchers("/api/v1/hotel/add-hotel").hasAnyRole("ADMIN", "SUPPERUSER")
                        .antMatchers("/api/v1/ratings/rate").hasAnyRole("USER", "ADMIN", "SUPPERUSER")
                        .antMatchers("/api/v1/roles/**").hasRole("SUPPERUSER")
                        .antMatchers("/api/v1/users/all_roles","/api/v1/users/profile/{userId}","/api/v1/users/delete/{userId}").hasAnyRole("ADMIN", "SUPPERUSER")
                        .antMatchers("/api/v1/rooms/admin/add/new-room","/api/v1/rooms/admin/update/{roomId}","/api/v1/rooms/delete/room/{roomId}").hasAnyRole("ADMIN", "SUPPERUSER")
                        .antMatchers("/api/v1/auth/user/register-user","/api/v1/auth/user/register-admin","/api/v1/auth/user/login","/api/v1/auth/user/reset-password","/api/v1/auth/user/confirm-reset-password",
                        "/api/v1/hotel/hotels/managed-by/{email}","/api/v1/hotel/hotels/all-hotels","/api/v1/hotel/{name}",
                        "/api/v1/rooms/all-rooms/**","/api/v1/rooms/room/types","/api/v1/rooms/room/{roomId}","/api/v1/rooms/room/{roomId}/reviews","/api/v1/rooms/available-rooms",
                        "/api/v1/users/{email}",
                        "/api/v1/booking/confirmation/{confirmationCode}").permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
