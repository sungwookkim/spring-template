package com.member.domain.repository;

import com.member.domain.entity.Member;

public interface MemberRepository {

    void save(Member member);

    Member findByName(String memberName);
}
