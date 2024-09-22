package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.UserAlreadyExistsException;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.request.LoginRequest;
import com.example.booking_hotel.response.JwtResponse;
import com.example.booking_hotel.security.jwt.JwtUtils;
import com.example.booking_hotel.security.user.HotelUserDetails;
import com.example.booking_hotel.service.Impl.EmailServiceImpl;
import com.example.booking_hotel.service.IUserService;
import com.example.booking_hotel.util.OTPGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        try{
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");

        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request){
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtTokenForUser(authentication);
        HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("Email not found");
            }
            String otp = OTPGenerator.generateOTP(6);
            userService.createPasswordResetTokenForUser(user, otp);
            String subject = "Password Reset Request";
            String content = "Your OTP for password reset is: " + otp;
            emailServiceImpl.sendEmail(email, subject, content);

            return ResponseEntity.ok("An email with OTP has been sent to your email address.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password: " + e.getMessage());
        }
    }
    @PostMapping("/confirm-reset-password")
    public ResponseEntity<?> confirmResetPassword(@RequestParam("email") String email,
                                                  @RequestParam("otp") String otp,
                                                  @RequestParam("newPassword") String newPassword) {
        try {
            boolean isOTPValid = userService.validateOTP(email, otp);
            if (!isOTPValid) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }
            userService.updatePassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password: " + e.getMessage());
        }
    }
    @PostMapping("/update-user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            user.setId(userId);
            userService.updateUser(user);
            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}