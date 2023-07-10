package project.trendpick_pro.domain.coupon.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class CouponNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.COUPON_NOT_FOUND;

    public CouponNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
