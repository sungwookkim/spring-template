package com.role.domain.repository;

import com.role.domain.entity.Role;

import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);

    Optional<Role> findById(Long id);
}
