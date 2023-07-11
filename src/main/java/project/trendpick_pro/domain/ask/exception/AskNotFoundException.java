package project.trendpick_pro.domain.ask.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class AskNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.ASK_NOT_FOUND;

    public AskNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
