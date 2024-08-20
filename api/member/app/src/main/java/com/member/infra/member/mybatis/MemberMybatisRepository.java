package com.member.infra.member.mybatis;

import com.member.domain.entity.Member;
import com.member.domain.repository.MemberRepository;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMybatisRepository extends MemberRepository {

    @Insert("""
        insert into member (
            name
            , age
        ) values (
            #{name}
            , #{age}
        );
    """)
    @Options(useGeneratedKeys = true, keyColumn = "member_id", keyProperty = "memberId")
    @Override
    void save(Member member);

    @Select("""
        select
            *
        from member
        where name = #{memberName}
    """)
    @Override
    Member findByName(String memberName);
}
