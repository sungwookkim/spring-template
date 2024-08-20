package com.member.service.member.read;

import com.member.QueryExecute;
import com.member.domain.entity.Member;
import com.member.service.member.command.MemberCommandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

@ActiveProfiles(value = {"member-app-test"})
@SpringBootTest
public class MemberJpaReadServiceImplTest {
    @Autowired
    MemberCommandService memberJpaCommandServiceImpl;

    @Autowired
    MemberReadService memberJpaReadServiceImpl;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from member");
    }

    @Test
    @DisplayName("jpa 서비스 회원 이름 검색")
    void member_jpa_findByName() {
        //give
        Member member = new Member("김성욱", 40);
        this.memberJpaCommandServiceImpl.save(member);

        //when
        String memberName = this.memberJpaReadServiceImpl.findByName(member.getName())
                .map(Member::getName)
                .orElse("");

        //then
        Assertions.assertEquals(member.getName(), memberName);
    }
}
