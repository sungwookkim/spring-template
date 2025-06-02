package com.product.infra.outboxEvent.mybatis;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.domain.repository.OutboxEventRepository;
import com.outboxEvent.enums.OutboxEventStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * MyBatis를 사용하여 Outbox 이벤트 데이터를 처리하기 위한 리포지토리 인터페이스입니다.
 * {@link OutboxEventRepository}를 확장하며, 데이터베이스 연동과 관련된 메서드를 제공합니다.
 */
@Mapper
public interface OutboxEventMybatisRepository extends OutboxEventRepository {
    /**
     * 주어진 {@link OutboxEvent} 객체들을 데이터베이스에 저장합니다.
     * 저장 과정에서 각 객체에 대해 자동 생성된 키가 설정됩니다.
     *
     * @param outboxEvents 저장할 {@link OutboxEvent} 객체들의 리스트.
     *                     리스트의 각 객체는 저장될 데이터에 대한 정보를 포함해야 합니다.
     */
    @Insert("""
    <script>
        insert into outbox_event(
            aggregate_type,
            aggregate_id,
            event_type,
            payload,
            status,
            source_system,
            message_key,
            created_at,
            updated_at
        ) values 
        <foreach collection="list" item="outboxEvent" separator=",">
        (
            #{outboxEvent.aggregateType}
            , #{outboxEvent.aggregateId}
            , #{outboxEvent.eventType}
            , #{outboxEvent.payload}
            , #{outboxEvent.status}
            , #{outboxEvent.sourceSystem}
            , #{outboxEvent.messageKey}
            , #{outboxEvent.createdAt}
            , #{outboxEvent.updatedAt}
        )
        </foreach>
        ;
    </script>
    """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void saveAll(@Param("list") List<OutboxEvent> outboxEvents);

    @Override
    @Select("""
        select *
        from outbox_event
        where status = #{status}
        order by created_at asc
        ;
    """)
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(@Param("status") OutboxEventStatus status);

    @Update("""
    <script>
        update outbox_event set
            status = #{status}
        where id in
        <foreach collection="list" item="outboxEvent" separator="," open="(" close=")">
            #{outboxEvent.id}
        </foreach>
    </script>
    """)
    void updateStatus(@Param("list") List<OutboxEvent> outboxEvents
            , @Param("status") OutboxEventStatus status);
}
