package com.member.service.role.read.jpa;

import com.member.config.transactional.annotaion.MemberReadTransactional;
import com.member.infra.role.jpa.RolePathJpaRepository;
import com.member.service.role.read.RolePathReadService;
import com.member.service.role.read.RoleReadService;
import com.role.domain.entity.Role;
import com.role.domain.entity.RolePath;
import com.role.domain.repository.RolePathRepository;
import com.role.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@MemberReadTransactional
@RequiredArgsConstructor
public class RolePathReadServiceImpl implements RolePathReadService {
    private final RolePathJpaRepository rolePathJpaRepository;

    @Override
    public Optional<RolePath> findById(Long id) {
        return this.rolePathJpaRepository.findById(id);
    }
}
