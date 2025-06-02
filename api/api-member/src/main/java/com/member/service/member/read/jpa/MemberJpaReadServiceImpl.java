package com.member.service.member.read.jpa;

import com.member.config.transactional.annotaion.MemberReadTransactional;
import com.member.domain.entity.Member;
import com.member.infra.member.jpa.MemberJpaRepository;
import com.member.service.member.read.MemberReadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@MemberReadTransactional
@AllArgsConstructor
public class MemberJpaReadServiceImpl implements MemberReadService {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findByName(String name) {
        return Optional.ofNullable(this.memberJpaRepository.findByName(name));
    }

}
