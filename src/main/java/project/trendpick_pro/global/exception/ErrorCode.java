package project.trendpick_pro.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MEMBER_NOT_FOUND(404, "MEMBER-001", "멤버가 존재하지 않는 경우"),
    MEMBER_NOT_MATCH(403, "MEMBER-002", "멤버 권한이 다를 경우"),
    MEMBER_ALREADY_EXIST(409, "MEMBER-003", "같은 멤버가 이미 존재하는 경우"),
    PRODUCT_NOT_FOUND(404, "PRODUCT-001", "상품이 존재 하지 않을 경우"),
    ORDERITEM_OUT(404, "ORDERITEM-001", "재고가 없을 경우"),
    ORDER_NOT_FOUND(404, "ORDER-001", "주문이 존재하지 않는 경우"),
    DOCUMENT_NOT_FOUND(404, "DOCUMENT-001", "검색 객체가 존재하지 않는 경우");

    private final int status;
    private final String code;
    private final String description;

    ErrorCode(int status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }
}
