package com.member.api.member.v2;

import com.member.api.member.v1.MemberV1Controller;
import com.member.api.member.v1.model.MemberV1Model.ReqMemberV1Save;
import com.member.api.member.v2.model.MemberV2Model.MemberV2ProcessCode;
import com.member.api.member.v2.model.MemberV2Model.ReqMemberV2Save;
import com.member.api.member.v2.model.MemberV2Model.RespV2FindMember;
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
@RequestMapping("v2")
@AllArgsConstructor
public class MemberV2JpaControllerImpl implements MemberV2Controller {
    private final MemberV1Controller memberV1MybatisControllerImpl;

    private final MemberCommandService memberJpaCommandServiceImpl;
    private final MemberReadService memberJpaReadServiceImpl;

    @Override
    public RestResponseResult<String> save(ReqMemberV2Save reqSave) {
        reqSave.validate();

        Member member = new Member(reqSave.getName(), reqSave.getAge());

        this.memberJpaCommandServiceImpl.save(member);

        return RestResponseResult.success();
    }

    @Override
    public RestResponseResult<String> save(ReqMemberV1Save reqSave) {
        return this.memberV1MybatisControllerImpl.save(reqSave);
    }

    @Override
    public RestResponseResult<RespV2FindMember> v2findMember(String name) {
        if("".equals(StringUtils.notBlankAndNotEmptyValidate(name))) {
            throw new ProcessException(MemberV2ProcessCode.BLANK_AND_EMPTY);
        }

        RespV2FindMember respV2FindMember = this.memberJpaReadServiceImpl.findByName(name)
                .map(v -> new RespV2FindMember(v.getName(), v.getAge()))
                .orElseGet(RespV2FindMember::empty);

        return RestResponseResult.success(respV2FindMember);
    }
}
