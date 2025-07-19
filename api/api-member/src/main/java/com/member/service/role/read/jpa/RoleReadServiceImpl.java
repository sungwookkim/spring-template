package com.member.service.role.read.jpa;

import com.member.config.transactional.annotaion.MemberReadTransactional;
import com.member.infra.role.jpa.RoleJpaRepository;
import com.member.service.role.read.RoleReadService;
import com.role.domain.entity.Role;
import com.role.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@MemberReadTransactional
@RequiredArgsConstructor
public class RoleReadServiceImpl implements RoleReadService {
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Optional<Role> findById(Long id) {
        return this.roleJpaRepository.findById(id);
    }
}
