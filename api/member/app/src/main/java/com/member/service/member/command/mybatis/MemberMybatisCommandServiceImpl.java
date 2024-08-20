package com.member.service.member.command.mybatis;

import com.member.config.transactional.annotaion.MemberWriteTransactional;
import com.member.domain.entity.Member;
import com.member.infra.member.mybatis.MemberMybatisRepository;
import com.member.service.member.command.MemberCommandService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@MemberWriteTransactional
@AllArgsConstructor
public class MemberMybatisCommandServiceImpl implements MemberCommandService {
    private final MemberMybatisRepository memberMybatisRepository;

    @Override
    public void save(Member member) {
        this.memberMybatisRepository.save(member);
    }
}
