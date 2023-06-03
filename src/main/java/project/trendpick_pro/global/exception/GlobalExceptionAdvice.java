package project.trendpick_pro.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(MemberNotFoundException.class)
    public String MemberNotFoundHandleException(MemberNotFoundException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "trendpick/usr/member/join";
    }

    @ExceptionHandler(MemberNotMatchException.class)
    public String MemberNotMatchHandleException(MemberNotMatchException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "trendpick/usr/member/join";
    }

    @ExceptionHandler(MemberAlreadyExistException.class)
    public String MemberAlreadyExistHandleException(MemberAlreadyExistException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "trendpick/usr/member/join";
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String ProductNotFoundHandleException(ProductNotFoundException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return "trendpick/usr/member/join";
    }
}
