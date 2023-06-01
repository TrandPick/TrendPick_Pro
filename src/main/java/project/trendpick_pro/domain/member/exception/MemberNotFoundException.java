package project.trendpick_pro.domain.member.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.ErrorCode;
import project.trendpick_pro.global.exception.BaseException;

public class MemberNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.MEMBER_NOT_FOUND;

    public MemberNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
