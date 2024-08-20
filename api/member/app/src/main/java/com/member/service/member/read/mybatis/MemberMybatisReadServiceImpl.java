package com.member.service.member.read.mybatis;

import com.member.config.transactional.annotaion.MemberReadTransactional;
import com.member.domain.entity.Member;
import com.member.infra.member.mybatis.MemberMybatisRepository;
import com.member.service.member.read.MemberReadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@MemberReadTransactional
@AllArgsConstructor
public class MemberMybatisReadServiceImpl implements MemberReadService {
    private final MemberMybatisRepository memberMybatisRepository;

    @Override
    public Optional<Member> findByName(String name) {
        return Optional.ofNullable(this.memberMybatisRepository.findByName(name));
    }
}
