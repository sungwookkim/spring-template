package com.member.service.role.read;

import com.role.domain.entity.RolePath;

import java.util.Optional;

public interface RolePathReadService {
    Optional<RolePath> findById(Long id);
}
