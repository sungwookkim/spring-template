package com.member.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.api.member.v2.model.MemberV2Model.ReqV2MemberSave;
import com.member.common.http.ProcessCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.member.api.member.v1.model.MemberV1Model.ReqV1MemberSave;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ComponentScan("com.member")
@ActiveProfiles(value = {"web-member-test"})
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[post] /v1/member")
    void memberV1Save() throws Exception {
        ReqV1MemberSave reqV1MemberSave = new ReqV1MemberSave();
        reqV1MemberSave.setName("김성욱");
        reqV1MemberSave.setAge(40);

        this.mockMvc.perform(post("/v1/member")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(reqV1MemberSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] 예외 /v1/member")
    void memberV1SaveException() throws Exception {
        ReqV1MemberSave reqV1MemberSave = new ReqV1MemberSave();
        /*
        이름에 값이 없는 경우
         */
        /*reqMemberV1Save.setAge(40);*/

        /*
        나이에 값이 없는 경우
         */
        /*reqMemberV1Save.setName("김성욱");*/

        /*
        전부 없는 경우
         */
        reqV1MemberSave.setName(null);
        reqV1MemberSave.setAge(null);

        this.mockMvc.perform(post("/v1/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqV1MemberSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(503))
                .andExpect(jsonPath("processCode").value("-10"))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] /v1/member")
    void memberV1FindMember() throws Exception {
        this.memberV1Save();

        String name = "김성욱1";

        this.mockMvc.perform(get("/v1/member?name=" + name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] 예외 필수 값 없음 /v1/member")
    void memberV1FindMemberException1() throws Exception {
        this.memberV1Save();

        this.mockMvc.perform(get("/v1/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(400))
                .andExpect(jsonPath("processCode").value("-9999"))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] 예외 필수 값 빈값 /v1/member")
    void memberV1FindMemberException2() throws Exception {
        this.memberV1Save();

        String name = "";

        this.mockMvc.perform(get("/v1/member?name=" + name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(503))
                .andExpect(jsonPath("processCode").value("-10"))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] /v2/member")
    void memberV2Save() throws Exception {
        ReqV2MemberSave reqV2MemberSave = new ReqV2MemberSave();
        reqV2MemberSave.setName("김성욱");
        reqV2MemberSave.setAge(40);

        this.mockMvc.perform(post("/v2/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqV2MemberSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] 예외 /v2/member")
    void memberV2SaveException() throws Exception {
        ReqV2MemberSave reqV2MemberSave = new ReqV2MemberSave();
        /*
        이름에 값이 없는 경우
         */
        /*reqMemberV2Save.setAge(40);*/

        /*
        나이에 값이 없는 경우
         */
        /*reqMemberV2Save.setName("김성욱");*/

        /*
        전부 없는 경우
         */
        reqV2MemberSave.setName(null);
        reqV2MemberSave.setAge(null);

        this.mockMvc.perform(post("/v2/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqV2MemberSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(503))
                .andExpect(jsonPath("processCode").value("-10"))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] /v2/v1/member")
    void memberV2MybatisSave() throws Exception {
        ReqV1MemberSave reqV1MemberSave = new ReqV1MemberSave();
        reqV1MemberSave.setName("김성욱");
        reqV1MemberSave.setAge(40);

        this.mockMvc.perform(post("/v2/v1/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqV1MemberSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] /v2/member")
    void memberV2FindMember() throws Exception {
        this.memberV2Save();

        String name = "김성욱";

        this.mockMvc.perform(get("/v2/member?name=" + name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] 예외 필수 값 없음 /v2/member")
    void memberV2FindMemberException1() throws Exception {
        this.memberV2Save();

        this.mockMvc.perform(get("/v2/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(400))
                .andExpect(jsonPath("processCode").value("-9999"))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] 예외 필수 값 빈값 /v2/member")
    void memberV2FindMemberException2() throws Exception {
        this.memberV2Save();

        String name = "";

        this.mockMvc.perform(get("/v2/member?name=" + name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(503))
                .andExpect(jsonPath("processCode").value("-10"))
                .andDo(print());
    }

    @Test
    @DisplayName("[get] 예외 URI 미지원 /v2/v1/member")
    void memberV2FindMemberException3() throws Exception {
        this.memberV2Save();

        String name = "김성욱";

        this.mockMvc.perform(get("/v2/v1/member?name=" + name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(503))
                .andExpect(jsonPath("processCode").value("-10001"))
                .andDo(print());
    }
}
