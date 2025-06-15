package com.member.infra.member.mybatis;

import com.member.domain.entity.Member;
import com.member.domain.repository.MemberRepository;
import org.apache.ibatis.annotations.*;

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

    @Update("""
    update member set
        name = #{name}
        , age = #{age}
    where member_id = #{memberId}
    """)
    int update(Member member);
}
