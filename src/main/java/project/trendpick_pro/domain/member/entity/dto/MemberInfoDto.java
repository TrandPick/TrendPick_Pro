package project.trendpick_pro.domain.member.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.trendpick_pro.domain.member.entity.Member;

@Getter
@NoArgsConstructor
@Setter
public class MemberInfoDto {

    private String bankAccount;
    private String address;
    private String phone;
    private String name;
    private String bankName;
    private String email;
    private Long memberId;

    @Builder
    public MemberInfoDto (Member member) {
        this.memberId = member.getId();
        this.name = member.getUsername();
        this.email = member.getEmail();
        this.phone = member.getPhoneNumber();
        this.address = member.getAddress();
        this.bankName = member.getBankName();
        this.bankAccount = member.getBankAccount();
    }

    public static MemberInfoDto of(Member member){
        return new MemberInfoDto(member);
    }
}
