package com.member.api.member.v1;

import com.member.api.member.v1.model.MemberV1Model.ReqMemberV1Save;
import com.member.api.member.v1.model.MemberV1Model.RespV1FindMember;
import com.member.common.http.RestResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface MemberV1Controller {

    @PostMapping("/member")
    RestResponseResult<String> save(@RequestBody ReqMemberV1Save reqSave);

    @GetMapping("/member")
    RestResponseResult<RespV1FindMember> findMember(@RequestParam("name") String name);
}
