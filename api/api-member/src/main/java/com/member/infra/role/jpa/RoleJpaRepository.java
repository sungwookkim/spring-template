package com.member.infra.role.jpa;

import com.role.domain.entity.Role;
import com.role.domain.repository.RoleRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface RoleJpaRepository extends Repository<Role, Long>, RoleRepository {
}
