package com.role.domain.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode // 복합키 클래스는 equals와 hashCode 구현이 필수
public class RolePathId implements Serializable {
    private Long ancestor;
    private Long descendant;
}