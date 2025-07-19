package com.role.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_path")
@Getter
@NoArgsConstructor
@IdClass(RolePathId.class) // 복합키 클래스 지정
public class RolePath {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_id")
    private Role ancestor;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descendant_id")
    private Role descendant;

    private int depth;

    public RolePath(Role ancestor, Role descendant, int depth) {
        this.ancestor = ancestor;
        this.descendant = descendant;
        this.depth = depth;
    }
}