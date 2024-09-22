package com.example.booking_hotel.service.Impl;

import com.example.booking_hotel.exception.UserAlreadyExistsException;
import com.example.booking_hotel.model.PasswordResetToken;
import com.example.booking_hotel.model.Role;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.respository.PasswordResetTokenRepository;
import com.example.booking_hotel.respository.RoleRepository;
import com.example.booking_hotel.respository.UserRepository;
import com.example.booking_hotel.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        Role userRole = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null) {
            userRepository.deleteByEmail(email);
        }

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User getUserProfile(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String otp) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(2));
        passwordResetToken.setEmail(user.getEmail());
        passwordResetToken.setOtp(otp);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public boolean validateOTP(String email, String otp) {
        Optional<PasswordResetToken> passwordResetTokenOptional = passwordResetTokenRepository.findTopByEmailOrderByExpiryDateDesc(email);

        if (passwordResetTokenOptional.isPresent()) {
            PasswordResetToken passwordResetToken = passwordResetTokenOptional.get();
            boolean isOTPValid = passwordResetToken.getOtp().equals(otp) &&
                    passwordResetToken.getExpiryDate().isAfter(LocalDateTime.now().minusMinutes(2));

            if (isOTPValid) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    @Override
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(existingUser);
    }

}



