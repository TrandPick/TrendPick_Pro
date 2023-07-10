package project.trendpick_pro.domain.ask.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class AskNotMatchException extends BaseException {

    private static final ErrorCode code = ErrorCode.ASK_NOT_MATCH;

    public AskNotMatchException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
