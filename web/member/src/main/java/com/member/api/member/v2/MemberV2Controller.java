package com.member.api.member.v2;

import com.member.api.member.v1.MemberV1Controller;
import com.member.api.member.v1.model.MemberV1Model.ReqMemberV1Save;
import com.member.api.member.v1.model.MemberV1Model.RespV1FindMember;
import com.member.api.member.v2.model.MemberV2Model.ReqMemberV2Save;
import com.member.api.member.v2.model.MemberV2Model.RespV2FindMember;
import com.member.common.exception.ProcessException;
import com.member.common.http.ProcessCode;
import com.member.common.http.RestResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface MemberV2Controller extends MemberV1Controller {

    @PostMapping("/member")
    RestResponseResult<String> save(@RequestBody ReqMemberV2Save reqSave);

    @GetMapping("/member")
    RestResponseResult<RespV2FindMember> v2findMember(@RequestParam("name")String name);

    @PostMapping("/v1/member")
    @Override
    RestResponseResult<String> save(@RequestBody ReqMemberV1Save reqSave);

    @GetMapping("/v1/member")
    @Override
    default RestResponseResult<RespV1FindMember> findMember(@RequestParam("name")String name) {
        throw new ProcessException(ProcessCode.Common.UNSUPPORTED_FEATURE);
    }
}
