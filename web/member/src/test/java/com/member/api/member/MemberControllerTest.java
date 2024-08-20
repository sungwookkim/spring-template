package com.member.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.api.member.v2.model.MemberV2Model.ReqMemberV2Save;
import com.member.common.http.ProcessCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.member.api.member.v1.model.MemberV1Model.ReqMemberV1Save;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles(value = {"member-api-test"})
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[post] /v1/member")
    void memberV1Save() throws Exception {
        ReqMemberV1Save reqMemberV1Save = new ReqMemberV1Save();
        reqMemberV1Save.setName("김성욱");
        reqMemberV1Save.setAge(40);

        this.mockMvc.perform(post("/v1/member")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(reqMemberV1Save)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] 예외 /v1/member")
    void memberV1SaveException() throws Exception {
        ReqMemberV1Save reqMemberV1Save = new ReqMemberV1Save();
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
        reqMemberV1Save.setName(null);
        reqMemberV1Save.setAge(null);

        this.mockMvc.perform(post("/v1/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqMemberV1Save)))
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
        ReqMemberV2Save reqMemberV2Save = new ReqMemberV2Save();
        reqMemberV2Save.setName("김성욱");
        reqMemberV2Save.setAge(40);

        this.mockMvc.perform(post("/v2/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqMemberV2Save)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("processCode").value(ProcessCode.Common.SUCCESS.getCode()))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] 예외 /v2/member")
    void memberV2SaveException() throws Exception {
        ReqMemberV2Save reqMemberV2Save = new ReqMemberV2Save();
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
        reqMemberV2Save.setName(null);
        reqMemberV2Save.setAge(null);

        this.mockMvc.perform(post("/v2/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqMemberV2Save)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("httpStatus.code").value(503))
                .andExpect(jsonPath("processCode").value("-10"))
                .andDo(print());
    }

    @Test
    @DisplayName("[post] /v2/v1/member")
    void memberV2MybatisSave() throws Exception {
        ReqMemberV1Save reqMemberV1Save = new ReqMemberV1Save();
        reqMemberV1Save.setName("김성욱");
        reqMemberV1Save.setAge(40);

        this.mockMvc.perform(post("/v2/v1/member")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(reqMemberV1Save)))
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
