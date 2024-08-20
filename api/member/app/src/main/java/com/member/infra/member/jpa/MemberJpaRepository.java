package com.member.infra.member.jpa;

import com.member.domain.entity.Member;
import com.member.domain.repository.MemberRepository;
import org.springframework.data.repository.Repository;

public interface MemberJpaRepository extends Repository<Member, Long>, MemberRepository {

}
