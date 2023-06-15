package project.trendpick_pro.domain.member.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import project.trendpick_pro.domain.member.entity.Member;

@Getter
@NoArgsConstructor
@Setter
public class MemberInfoDto {
    @JsonProperty("bankAccount")
    private String bankAccount;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("name")
    private String name;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("memberId")
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

    @Override
    public String toString() {
        return "MemberInfoDto{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                '}';
    }
}
