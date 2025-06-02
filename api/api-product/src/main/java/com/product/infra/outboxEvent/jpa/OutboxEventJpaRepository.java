package com.product.infra.outboxEvent.jpa;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.domain.repository.OutboxEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * OutboxEventJpaRepository 인터페이스는 OutboxEventRepository 및 JpaRepository를 상속하여
 * OutboxEvent 엔티티에 대한 데이터 액세스 및 기본 CRUD 기능을 제공합니다.
 *
 * 이 인터페이스는 Spring Data JPA를 통해 구현되며, 데이터베이스와 상호작용하여
 * Outbox 이벤트를 관리하기 위한 메서드를 제공합니다.
 *
 * JpaRepository를 확장함으로써 페이징, 정렬, CRUD 등의 기능을 즉시 사용할 수 있습니다.
 * 또한 OutboxEventRepository를 상속하여 커스텀 메서드를 추가로 정의합니다.
 */
@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, Long>, OutboxEventRepository {
}
