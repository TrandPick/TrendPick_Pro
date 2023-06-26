package project.trendpick_pro.global.search.exception;

import org.springframework.http.HttpStatus;
import project.trendpick_pro.global.exception.BaseException;
import project.trendpick_pro.global.exception.ErrorCode;

public class DocumentNotFoundException extends BaseException {

    private static final ErrorCode code = ErrorCode.PRODUCT_NOT_FOUND;

    public DocumentNotFoundException(String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
