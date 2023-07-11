package project.trendpick_pro.domain.notification.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class NotificationNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.NOTIFICATION_NOT_FOUND;

    public NotificationNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
