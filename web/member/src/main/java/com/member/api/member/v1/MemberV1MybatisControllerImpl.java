package com.member.api.member.v1;

import com.member.api.member.v1.model.MemberV1Model.MemberV1ProcessCode;
import com.member.api.member.v1.model.MemberV1Model.ReqMemberV1Save;
import com.member.api.member.v1.model.MemberV1Model.RespV1FindMember;
import com.member.common.exception.ProcessException;
import com.member.common.helper.lang.StringUtils;
import com.member.common.http.RestResponseResult;
import com.member.domain.entity.Member;
import com.member.service.member.command.MemberCommandService;
import com.member.service.member.read.MemberReadService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1")
@AllArgsConstructor
public class MemberV1MybatisControllerImpl implements MemberV1Controller {
    private final MemberCommandService memberMybatisCommandServiceImpl;
    private final MemberReadService memberMybatisReadServiceImpl;

    @Override
    public RestResponseResult<String> save(ReqMemberV1Save reqSave) {
        reqSave.validate();

        Member member = new Member(reqSave.getName(), reqSave.getAge());

        this.memberMybatisCommandServiceImpl.save(member);

        return RestResponseResult.success();
    }

    @Override
    public RestResponseResult<RespV1FindMember> findMember(String name) {
        if("".equals(StringUtils.notBlankAndNotEmptyValidate(name))) {
            throw new ProcessException(MemberV1ProcessCode.BLANK_AND_EMPTY);
        }

        RespV1FindMember respV1FindMember = this.memberMybatisReadServiceImpl.findByName(name)
                .map(v -> new RespV1FindMember(v.getName(), v.getAge()))
                .orElseGet(RespV1FindMember::empty);

        return RestResponseResult.success(respV1FindMember);
    }
}
