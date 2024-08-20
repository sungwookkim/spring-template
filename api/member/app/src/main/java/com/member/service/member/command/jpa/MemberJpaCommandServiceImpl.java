package com.member.service.member.command.jpa;

import com.member.config.transactional.annotaion.MemberWriteTransactional;
import com.member.domain.entity.Member;
import com.member.infra.member.jpa.MemberJpaRepository;
import com.member.service.member.command.MemberCommandService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@MemberWriteTransactional
@AllArgsConstructor
public class MemberJpaCommandServiceImpl implements MemberCommandService {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public void save(Member member) {
        this.memberJpaRepository.save(member);
    }
}
