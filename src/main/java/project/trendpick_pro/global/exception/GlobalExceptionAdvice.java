package project.trendpick_pro.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.trendpick_pro.domain.common.base.rq.Rq;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    private final Rq rq;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(MemberNotFoundException.class)
    public String MemberNotFoundHandleException(HttpServletRequest request, MemberNotFoundException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return request.getRequestURI();
    }

    @ExceptionHandler(MemberNotMatchException.class)
    public String MemberNotMatchHandleException(HttpServletRequest request, MemberNotMatchException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return request.getRequestURI();
    }

    @ExceptionHandler(MemberAlreadyExistException.class)
    public String MemberAlreadyExistHandleException(HttpServletRequest request, MemberAlreadyExistException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return request.getRequestURI();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String ProductNotFoundHandleException(HttpServletRequest request, ProductNotFoundException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return request.getRequestURI();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException() {
        return rq.historyBack(maxFileSize + " 이내 용량의 파일들만 업로드 할 수 있습니다.");
    }
}
