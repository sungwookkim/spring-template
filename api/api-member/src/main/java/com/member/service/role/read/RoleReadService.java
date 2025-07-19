package com.member.service.role.read;

import com.role.domain.entity.Role;

import java.util.Optional;

public interface RoleReadService {
    Optional<Role> findById(Long id);
}
