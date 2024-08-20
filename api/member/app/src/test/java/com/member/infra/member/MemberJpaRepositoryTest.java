package com.member.infra.member;

import com.member.domain.entity.Member;
import com.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberJpaRepositoryTest {
    @Autowired
    MemberRepository memberJpaRepository;

    @Test
    @DisplayName("회원 저장")
    void member_save() {
        //give
        Member member = new Member("김성욱", 40);

        //when
        this.memberJpaRepository.save(member);

        //then
        Assertions.assertTrue(member.getMemberId() > 0);
    }
}
