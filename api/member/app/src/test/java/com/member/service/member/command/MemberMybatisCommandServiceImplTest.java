package com.member.service.member.command;

import com.member.QueryExecute;
import com.member.domain.entity.Member;
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
public class MemberMybatisCommandServiceImplTest {
    @Autowired MemberCommandService memberMybatisCommandServiceImpl;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from member");
    }

    @Test
    @DisplayName("mybatis 서비스 회원 저장")
    void member_mybatis_save() {
        //give
        Member member = new Member("김성욱", 40);

        //when
        this.memberMybatisCommandServiceImpl.save(member);

        //then
        Assertions.assertTrue(member.getMemberId() > 0);
    }

}
