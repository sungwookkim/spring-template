package com.member.service.member.read;

import com.member.QueryExecute;
import com.member.domain.entity.Member;
import com.member.service.member.command.MemberCommandService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

@ActiveProfiles(value = {"member-app-test"})
@SpringBootTest
public class MemberMybatisReadServiceImplTest {
    @Autowired
    MemberCommandService memberMybatisCommandServiceImpl;

    @Autowired
    MemberReadService memberMybatisReadServiceImpl;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from member");
    }

    @Test
    @DisplayName("mybatis 서비스 회원 이름 검색")
    void member_mybatis_findByName() {
        //give
        Member member = new Member("김성욱", 40);
        this.memberMybatisCommandServiceImpl.save(member);

        //when
        String memberName = this.memberMybatisReadServiceImpl.findByName(member.getName())
                .map(Member::getName)
                .orElse("");

        //then
        Assertions.assertEquals(member.getName(), memberName);
    }
}
