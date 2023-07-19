package project.trendpick_pro.global.redis.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class RedisLockException extends BaseException {

    private static final ErrorCode code = ErrorCode.LOCK_ALREADY_USED;

    public RedisLockException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
