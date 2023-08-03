package project.trendpick_pro.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.member.entity.form.JoinForm;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.TagType;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member",
        indexes = {@Index(name = "index_member_email",  columnList="email", unique = true)})
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email",unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRoleType role;

    private String brand;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<FavoriteTag> tags = new LinkedHashSet<>();

    private String bankName;
    private String bankAccount;

    private String address;
    private LocalDateTime recentlyAccessDate;
    private long restCash;

    @Builder
    private Member(String email, String password, String username, String phoneNumber, MemberRoleType role, String brand) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.brand = brand;
    }

    public static Member of(JoinForm joinForm, String password, MemberRoleType role) {
        return Member.builder()
                .email(joinForm.email())
                .password(password)
                .username(joinForm.username())
                .phoneNumber(joinForm.phoneNumber())
                .role(role)
                .build();
    }

    public void connectBrand(String brand){
        this.brand = brand;
    }

    public void connectAddress(String address) {
        this.address = address;
    }

    public void connectBank(String bankName, String bankAccount) {
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }

    public void connectCash(long cash){
        this.restCash = cash;
    }

    public void changeTags(Set<FavoriteTag> tags) {
        this.tags.clear();
        tags.forEach(tag -> {
            tag.connectMember(this);
            tag.increaseScore(TagType.REGISTER);
        });
        this.tags.addAll(tags);

    }

    public void addTag(FavoriteTag tag){
        getTags().add(tag);
        tag.connectMember(this);
    }

    public void updateRecentlyAccessDate(LocalDateTime dateTime) {
        this.recentlyAccessDate = dateTime;
    }
}
