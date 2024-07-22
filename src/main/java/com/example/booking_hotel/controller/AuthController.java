package com.example.booking_hotel.controller;

import com.example.booking_hotel.exception.UserAlreadyExistsException;
import com.example.booking_hotel.model.Hotel;
import com.example.booking_hotel.model.Role;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.request.LoginRequest;
import com.example.booking_hotel.respo.Respose.JwtResponse;
import com.example.booking_hotel.security.jwt.JwtUtils;
import com.example.booking_hotel.security.user.HotelUserDetails;
import com.example.booking_hotel.service.EmailService;
import com.example.booking_hotel.service.HotelService;
import com.example.booking_hotel.service.IUserService;
import com.example.booking_hotel.service.RoleService;
import com.example.booking_hotel.util.ImageGeneral;
import com.example.booking_hotel.util.OTPGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static com.example.booking_hotel.util.ImageGeneral.decodeBase64ToImage;

@CrossOrigin

@RestController
@RequestMapping("/api/v1/auth/user")
@RequiredArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private final RoleService roleService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Fetch the ROLE_USER role from the database
            Role userRole = roleService.findByName(Role.RoleName.valueOf(Role.RoleName.ROLE_USER.name()));

            // Set the user's role to ROLE_USER
            user.setRoles(Collections.singletonList(userRole));

            // Register the user
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {
        try {
            // Fetch the ROLE_ADMIN role from the database
            Role adminRole = roleService.findByName(Role.RoleName.valueOf(Role.RoleName.ROLE_ADMIN.name()));

            // Set the user's role to ROLE_ADMIN
            user.setRoles(Collections.singletonList(adminRole));
            // Register the user
           userService.registerAdmin(user);
            return ResponseEntity.ok("Registration successful!");

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
            // Kiểm tra xem email có tồn tại trong hệ thống không
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("Email not found");
            }

            String otp = OTPGenerator.generateOTP(6);
            userService.createPasswordResetTokenForUser(user, otp);

            String subject = "Password Reset Request";
            String base64Avatar = user.getAvatar();
            byte[] imageBytes = null;
            if (base64Avatar != null) {
                imageBytes = decodeBase64ToImage(base64Avatar);
            }

            String content = "<html><head><style>";
            content += "body {font-family: Arial, sans-serif;}";
            content += "h1 {color: #333333;}";
            content += "table {width: 100%; border-collapse: collapse;}";
            content += "th, td {border: 1px solid #dddddd; text-align: left; padding: 8px;}";
            content += "th {background-color: #f2f2f2;}";
            content += "</style></head><body>";
            content += "<h1>Password Reset Request</h1>";
            content += "<p>Hello " + user.getFirstName() + ",</p>";
            content += "<p>You have requested to reset your password. Please use the following OTP to reset your password:</p>";
            content += "<table>";
            content += "<tr><th>OTP</th><td>" + otp + "</td></tr>";
            content += "</table>";

            if (imageBytes != null) {
                content += "<p>Your avatar:</p>";
                content += "<img src='cid:avatarPhoto' alt='Avatar Image' style='width: 100px; height: auto;'>";
            }

            content += "<p>Thank you for using our service.</p>";
            content += "</body></html>";

            // Gửi email với hoặc không có ảnh đại diện
            emailService.sendEmail(email, subject, content, imageBytes);

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
            // Kiểm tra mã OTP có hợp lệ không
            boolean isOTPValid = userService.validateOTP(email, otp);
            if (!isOTPValid) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }

            // Cập nhật mật khẩu mới cho người dùng
            userService.updatePassword(email, newPassword);

            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password: " + e.getMessage());
        }
    }
    @PostMapping("/update-user/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestParam("user") String userJson,
                                        @RequestParam("avatar") MultipartFile avatar) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(userJson, User.class);
            user.setId(userId);

            if (!avatar.isEmpty()) {
                InputStream inputStream = avatar.getInputStream();
                String base64Avatar = ImageGeneral.fileToBase64(inputStream);
                user.setAvatar(base64Avatar);
            }

            userService.updateUser(user);
            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}