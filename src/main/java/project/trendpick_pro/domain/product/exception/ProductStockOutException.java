package project.trendpick_pro.domain.product.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class ProductStockOutException extends BaseException {

    private static final ErrorCode code = ErrorCode.ORDERITEM_OUT;

    public ProductStockOutException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
