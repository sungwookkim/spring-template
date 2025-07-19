package com.member.infra.role.jpa;

import com.role.domain.entity.RolePath;
import com.role.domain.entity.RolePathId;
import com.role.domain.repository.RolePathRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

@org.springframework.stereotype.Repository
public interface RolePathJpaRepository extends Repository<RolePath, RolePathId>, RolePathRepository {

    // 권한 추가 시, 부모의 경로를 자신과 연결하여 한번에 INSERT
    @Modifying
    @Query(value = """
        INSERT INTO role_path (ancestor_id, descendant_id, depth)
        SELECT p.ancestor_id, :descendantId, p.depth + 1
        FROM role_path p WHERE p.descendant_id = :ancestorId
    """, nativeQuery = true)
    void insertNewPaths(@Param("ancestorId") Long ancestorId, @Param("descendantId") Long descendantId);


    // 권한 이동 시, 이동할 노드의 기존 경로 삭제 (self-reference 제외)
    @Modifying
    @Query(value = """
        DELETE FROM role_path WHERE descendant_id = :descendantId AND depth > 0
    """, nativeQuery = true)
    void deleteOldPaths(@Param("descendantId") Long descendantId);

    // 권한 이동 시, 이동할 노드와 그 자손들을 새 부모와 연결 (더욱 견고한 버전)
    @Modifying
    @Query(value = """
        INSERT INTO role_path (ancestor_id, descendant_id, depth)
        SELECT p.ancestor_id, c.descendant_id, p.depth + c.depth + 1
        FROM role_path p, role_path c
        WHERE p.descendant_id = :newParentId AND c.ancestor_id = :movedNodeId
    """, nativeQuery = true)
    void reconnectPaths(@Param("newParentId") Long newParentId, @Param("movedNodeId") Long movedNodeId);
}
