package com.example.booking_hotel.respository;
import com.example.booking_hotel.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {


    Optional<PasswordResetToken> findFirstByEmail(String email); // Change the method name and return type
    Optional<PasswordResetToken> findTopByEmailOrderByExpiryDateDesc(String email);
    boolean existsByEmailAndExpiryDateAfter(String email, LocalDateTime expiryDate);

}
