package project.trendpick_pro.domain.member.entity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.member.entity.Member;

@Getter
@NoArgsConstructor
public class MemberInfoDto {

    private Long memberId;
    private String name;
    private String email;

    public MemberInfoDto(Member member) {
        this.memberId = member.getId();
        this.name = member.getUsername();
        this.email = member.getEmail();
        this.phone = member.getPhoneNumber();
        this.address = member.getAddress();
    }

    private String phone;
    private String address;
}
