package com.member.service.role.command.jpa;

import com.member.config.transactional.annotaion.MemberWriteTransactional;
import com.member.infra.role.jpa.RolePathJpaRepository;
import com.member.service.role.command.RoleCommandService;
import com.member.service.role.read.RoleReadService;
import com.role.domain.entity.Role;
import com.role.domain.entity.RolePath;
import com.role.domain.repository.RolePathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@MemberWriteTransactional
@RequiredArgsConstructor
public class RoleJpaCommandServiceImpl implements RoleCommandService {
    private final RolePathJpaRepository rolePathJpaRepository;

    private final RoleReadService roleReadJapService;

    @Override
    public Role addRole(Long myId, Long parentId) {
        Role myRole = this.roleReadJapService.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));

        // 2. 자기 자신을 가리키는 경로(depth=0) 추가
        this.rolePathJpaRepository.save(new RolePath(myRole, myRole, 0));

        // 3. 부모가 있으면, 부모의 경로를 상속받아 새로운 경로 추가
        if (parentId != null) {
            Role parentRole = this.roleReadJapService.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("상위 계정이 존재하지 않습니다."));

            this.rolePathJpaRepository.insertNewPaths(parentRole.getId(), myRole.getId());
        }

        return myRole;
    }

    @Override
    public void moveRole(Long myId, Long newParentId) {
        Role movedRole = this.roleReadJapService.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));
        Role newParentRole = this.roleReadJapService.findById(newParentId)
                .orElseThrow(() -> new IllegalArgumentException("변경하려는 계정이 존재하지 않습니다."));

        // 1. 기존 경로 삭제 (Disconnect)
        this.rolePathJpaRepository.deleteOldPaths(movedRole.getId());

        // 2. 새로운 경로 추가 (Reconnect)
        // 이 쿼리는 이동할 노드가 자식을 가졌을 때도 모든 하위 경로를 한번에 재계산합니다.
        this.rolePathJpaRepository.reconnectPaths(newParentRole.getId(), movedRole.getId());
    }
}
