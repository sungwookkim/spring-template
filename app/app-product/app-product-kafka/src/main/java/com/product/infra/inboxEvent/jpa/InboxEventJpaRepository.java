package com.product.infra.inboxEvent.jpa;

import com.inboxEvent.domain.entity.InboxEvent;
import com.inboxEvent.domain.repository.InboxEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * InboxEvent 엔터티에 대한 데이터 접근 작업을 처리하는 JPA 레포지토리 인터페이스.
 *
 * 이 인터페이스는 Spring Data JPA의 JpaRepository와 도메인 레포지토리 인터페이스인
 * InboxEventRepository를 결합하여 InboxEvent 엔터티를 관리하는 기능을 제공합니다.
 *
 * 주요 기능:
 * - 기본적인 CRUD 연산 제공 (JpaRepository 상속)
 * - 커스텀 비즈니스 로직 연산 지원 (InboxEventRepository 상속)
 *
 * 멱등성을 보장하기 위해 InboxEvent는 고유 식별자인 messageId를 사용합니다.
 */
@Repository
public interface InboxEventJpaRepository extends JpaRepository<InboxEvent, Long>, InboxEventRepository {

}
