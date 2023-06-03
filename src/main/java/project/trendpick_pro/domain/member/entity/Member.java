package project.trendpick_pro.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import project.trendpick_pro.domain.FavoriteTag;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FavoriteTag> tags = new ArrayList<>();

    private String bankName;
    private String bankAccount;

    private String address;

    // 이 함수 자체는 만들어야 한다. 스프링 시큐리티 규격
    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // 모든 멤버는 member 권한을 가진다.
        grantedAuthorities.add(new SimpleGrantedAuthority("member"));

        // username이 admin인 회원은 추가로 admin 권한도 가진다.
        if ("admin".equals(username)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
        }

        return grantedAuthorities;
    }

    public void connectAddress(String address) {
        this.address = address;
    }

    public void connectBank(String bankName, String bankAccount) {
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }

    public void changeTags(List<FavoriteTag> tags) {
        this.tags = tags;
    }
}
