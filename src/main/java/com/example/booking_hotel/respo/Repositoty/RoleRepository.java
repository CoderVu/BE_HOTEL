package com.example.booking_hotel.respo.Repositoty;

import com.example.booking_hotel.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);

    boolean existsByName(Role.RoleName name);
}