package com.member.api.member.v2.model;

import com.member.common.exception.ProcessException;
import com.member.common.helper.lang.StringUtils;
import com.member.common.http.ProcessCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class MemberV2Model {

    @Data
    @NoArgsConstructor
    public static class ReqMemberV2Save {
        private String name;
        private Integer age;
        public void validate() {
            if("".equals(StringUtils.notBlankAndNotEmptyValidate(name))) {
                throw new ProcessException(MemberV2ProcessCode.BLANK_AND_EMPTY);
            }

            if(Optional.ofNullable(age).filter(v -> v > 0).isEmpty()) {
                throw new ProcessException(MemberV2ProcessCode.BLANK_AND_EMPTY);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RespV2FindMember {
        private final String name;
        private final Integer age;

        public static RespV2FindMember empty() {
            return new RespV2FindMember("", 0);
        }
    }

    public enum MemberV2ProcessCode implements ProcessCode {
        BLANK_AND_EMPTY("-10");

        private final String code;

        MemberV2ProcessCode(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return this.code;
        }
    }
}
