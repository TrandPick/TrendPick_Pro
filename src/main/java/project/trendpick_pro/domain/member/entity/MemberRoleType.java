package project.trendpick_pro.domain.member.entity;

import lombok.Getter;
import project.trendpick_pro.domain.coupon.entity.expirationPeriod.ExpirationType;

import java.util.List;

@Getter
public enum MemberRoleType {
    ADMIN("ADMIN"),
    BRAND_ADMIN("BRAND_ADMIN"),
    MEMBER("MEMBER");

    private String value;

    MemberRoleType(String value) {
        this.value = value;
    }

    public static MemberRoleType getRoleType(String value){
        return switch (value) {
            case "ADMIN" -> ADMIN;
            case "BRAND_ADMIN" -> BRAND_ADMIN;
            case "MEMBER" -> MEMBER;
            default -> null;
        };
    }

    public static boolean isType(MemberRoleType memberRoleType) {
        return List.of(ADMIN, BRAND_ADMIN, MEMBER).contains(memberRoleType);
    }
}
