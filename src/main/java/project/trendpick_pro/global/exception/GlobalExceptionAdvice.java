package project.trendpick_pro.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> MEmberNotFoundHandleException(MemberNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[exceptionHandle] ex", e);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MemberNotMatchException.class)
    public ResponseEntity<ErrorResponse> MemberNotMatchHandleException(MemberNotMatchException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[exceptionHandle] ex", e);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MemberAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> MemberAlreadyExistHandleException(MemberAlreadyExistException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[exceptionHandle] ex", e);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> ProductNotFoundHandleException(ProductNotFoundException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[exceptionHandle] ex", e);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }
}
