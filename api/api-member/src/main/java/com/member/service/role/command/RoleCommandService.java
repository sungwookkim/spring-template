package com.member.service.role.command;

import com.role.domain.entity.Role;

public interface RoleCommandService {

    Role addRole(Long myId, Long parentId);

    void moveRole(Long myId, Long newParentId);
}
