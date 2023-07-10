package project.trendpick_pro.domain.member.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum RoleType{
    ADMIN("ADMIN"),
    BRAND_ADMIN("BRAND_ADMIN"),
    MEMBER("MEMBER");

    private String value;

    RoleType(String value) {
        this.value = value;
    }

    public static RoleType getRoleType(String value){
        return switch (value) {
            case "ADMIN" -> ADMIN;
            case "BRAND_ADMIN" -> BRAND_ADMIN;
            case "MEMBER" -> MEMBER;
            default -> null;
        };
    }
}
