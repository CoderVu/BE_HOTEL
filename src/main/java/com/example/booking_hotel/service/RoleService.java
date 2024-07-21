package com.example.booking_hotel.service;

import com.example.booking_hotel.exception.RoleAlreadyExistException;
import com.example.booking_hotel.exception.UserAlreadyExistsException;
import com.example.booking_hotel.model.Role;
import com.example.booking_hotel.model.User;
import com.example.booking_hotel.respo.Repositoty.RoleRepository;
import com.example.booking_hotel.respo.Repositoty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role theRole) {
        if (roleRepository.existsByName(theRole.getName())) {
            throw new RoleAlreadyExistException(theRole.getName() + " already exists");
        }
        return roleRepository.save(theRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(Role.RoleName name) { // cập nhật kiểu tham số
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }
    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (role.getUsers().contains(user)) {
            role.removeUserFromRole(user);
            roleRepository.save(role);
            return user;
        }

        throw new UsernameNotFoundException("User not found in role");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (user.getRoles().contains(role)) {
            throw new UserAlreadyExistsException(
                    user.getFirstName() + " is already assigned to the " + role.getName() + " role");
        }

        role.assignRoleToUser(user);
        roleRepository.save(role);
        return user;
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.removeAllUsersFromRole();
        return roleRepository.save(role);
    }
}
