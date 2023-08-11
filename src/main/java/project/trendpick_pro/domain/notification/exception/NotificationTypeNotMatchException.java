package project.trendpick_pro.domain.notification.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class NotificationTypeNotMatchException extends BaseException {

    private static final ErrorCode code = ErrorCode.NOTIFICATIONTYPE_NOT_MATCH;

    public NotificationTypeNotMatchException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
