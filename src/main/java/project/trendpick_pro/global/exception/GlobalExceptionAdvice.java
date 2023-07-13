package project.trendpick_pro.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import project.trendpick_pro.domain.ask.exception.AskNotFoundException;
import project.trendpick_pro.domain.ask.exception.AskNotMatchException;
import project.trendpick_pro.domain.coupon.exception.CouponNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberAlreadyExistException;
import project.trendpick_pro.domain.member.exception.MemberNotFoundException;
import project.trendpick_pro.domain.member.exception.MemberNotMatchException;
import project.trendpick_pro.domain.notification.exception.NotificationNotFoundException;
import project.trendpick_pro.domain.orders.exceoption.OrderItemNotFoundException;
import project.trendpick_pro.domain.orders.exceoption.OrderNotFoundException;
import project.trendpick_pro.domain.product.exception.ProductNotFoundException;
import project.trendpick_pro.domain.product.exception.ProductStockOutException;
import project.trendpick_pro.global.util.rq.Rq;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    private final Rq rq;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler({MemberNotFoundException.class, ProductNotFoundException.class,
                        CouponNotFoundException.class, OrderItemNotFoundException.class,
                        AskNotFoundException.class, OrderNotFoundException.class,
                        NotificationNotFoundException.class})
    public String NotFoundHandleException(HttpServletRequest request, BaseException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return request.getRequestURI();
    }

    @ExceptionHandler({MemberNotMatchException.class, AskNotMatchException.class})
    public String NotMatchHandleException(HttpServletRequest request, BaseException e, Model model) {
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

    @ExceptionHandler(ProductStockOutException.class)
    public String ProductStockOutHandleException(HttpServletRequest request, ProductStockOutException e, Model model) {
        log.error("[exceptionHandle] ex", e);
        model.addAttribute("errorMessage", e.getMessage());
        return request.getRequestURI();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException() {
        return rq.historyBack(maxFileSize + " 이내 용량의 파일들만 업로드 할 수 있습니다.");
    }
}
