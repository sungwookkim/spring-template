package com.member.service.member.read;

import com.member.domain.entity.Member;

import java.util.Optional;

public interface MemberReadService {
    Optional<Member> findByName(String name);
}
