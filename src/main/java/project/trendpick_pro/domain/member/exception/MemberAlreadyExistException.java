package project.trendpick_pro.domain.member.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.ErrorCode;
import project.trendpick_pro.global.exception.baseException;

public class MemberAlreadyExistException extends baseException {

    private static final ErrorCode code = ErrorCode.MEMBER_ALREADY_EXIST;

    public MemberAlreadyExistException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
