package com.role.domain.repository;

import com.role.domain.entity.RolePath;

import java.util.Optional;

public interface RolePathRepository {
    Optional<RolePath> findById(Long id);

    RolePath save(RolePath rolePath);

    void insertNewPaths(Long ancestorId, Long descendantId);

    // 권한 이동 시, 이동할 노드의 기존 경로 삭제 (self-reference 제외)
    void deleteOldPaths(Long descendantId);

    // 권한 이동 시, 이동할 노드와 그 자손들을 새 부모와 연결 (더욱 견고한 버전)
    void reconnectPaths(Long newParentId, Long movedNodeId);
}
