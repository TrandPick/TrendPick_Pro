package project.trendpick_pro.domain.orders.exceoption;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class OrderNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.ORDER_NOT_FOUND;

    public OrderNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }

}