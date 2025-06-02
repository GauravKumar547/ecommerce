package org.example.userauthservice.services;

import org.example.userauthservice.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role createRole(Role role);
    Optional<Role> getRoleByName(String name);
    List<Role> getAllRoles();
    void deleteRole(Long id);
    Role updateRole(Long id, Role role);
} 