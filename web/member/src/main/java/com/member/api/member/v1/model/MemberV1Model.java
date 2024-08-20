package com.member.api.member.v1.model;

import com.member.common.exception.ProcessException;
import com.member.common.helper.lang.StringUtils;
import com.member.common.http.ProcessCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class MemberV1Model {

    @Data
    @NoArgsConstructor
    public static class ReqMemberV1Save {
        private String name;
        private Integer age;

        public void validate() {
            if("".equals(StringUtils.notBlankAndNotEmptyValidate(name))) {
                throw new ProcessException(MemberV1ProcessCode.BLANK_AND_EMPTY);
            }

            if(Optional.ofNullable(age).filter(v -> v > 0).isEmpty()) {
                throw new ProcessException(MemberV1ProcessCode.BLANK_AND_EMPTY);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RespV1FindMember {
        private final String name;
        private final Integer age;

        public static RespV1FindMember empty() {
            return new RespV1FindMember("", 0);
        }
    }

    public enum MemberV1ProcessCode implements ProcessCode {
        BLANK_AND_EMPTY("-10");

        private final String code;

        MemberV1ProcessCode(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return this.code;
        }
    }
}
