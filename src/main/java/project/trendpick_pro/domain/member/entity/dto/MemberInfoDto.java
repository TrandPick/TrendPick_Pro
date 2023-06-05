package project.trendpick_pro.domain.member.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoDto {

    private String name;
    private String email;

    public MemberInfoDto() {
    }

    public MemberInfoDto(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    private String phone;
    private String address;
}
