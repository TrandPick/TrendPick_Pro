package project.trendpick_pro.global.util.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.trendpick_pro.domain.cart.entity.CartItem;

@Getter
@AllArgsConstructor
public class RsData<T> {
    private String resultCode;
    private String msg;
    private T data;

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    public static <T> RsData<T> of(String resultCode, String msg) {
        return of(resultCode, msg, null);
    }

    public static <T> RsData<T> successOf(T data) {
        return of("S-1", "성공", data);
    }

    public static RsData success() {
        return of("S-1", "성공");
    }

    public static <T> RsData<T> failOf(T data) {
        return of("F-1", "실패", data);
    }

    public boolean isSuccess() {
        return resultCode.startsWith("S-");
    }

    public boolean isFail() {
        return !isSuccess();
    }
}
