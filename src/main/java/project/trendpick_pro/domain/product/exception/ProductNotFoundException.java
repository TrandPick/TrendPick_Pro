package project.trendpick_pro.domain.product.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class ProductNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.PRODUCT_NOT_FOUND;

    public ProductNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
