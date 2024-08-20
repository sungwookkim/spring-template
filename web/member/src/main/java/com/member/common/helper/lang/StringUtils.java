package com.member.common.helper.lang;

import java.util.Optional;
import java.util.function.Function;

/**
 * org.apache.commons.lang 패키지에 있는 코드 추출.
 */
public class StringUtils {
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 원본 클래스가 아닌 자체적으로 생성한 메서드
     * 매개변수로 받은 Object 타입을 String::valueOf를 통핸 빈값 여부등을 판단하여
     * 값이 없으면 빈 값을 반환.
     */
    public static String notBlankAndNotEmptyValidate(Object value) {
        Function<Optional<Object>, String> stringEmpty = f -> f
                .map(String::valueOf)
                .filter(StringUtils::isNotBlank)
                .filter(StringUtils::isNotEmpty)
                .orElse("");

        return stringEmpty.apply(Optional.ofNullable(value));
    }
}
