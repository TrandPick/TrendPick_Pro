package project.trendpick_pro.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

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
    private RoleType role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavoriteTag> tags = new LinkedHashSet<>();

    private String bankName;
    private String bankAccount;

    private String address;

    @Builder
    public Member(String email, String password, String username, String phoneNumber, RoleType role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public void connectAddress(String address) {
        this.address = address;
    }

    public void connectBank(String bankName, String bankAccount) {
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }

    public void changeTags(Set<FavoriteTag> tags) {
        this.tags = tags;
        for(FavoriteTag tag : tags)
            tag.connectMember(this);
    }

    //양방향 메서드
    public void addTag(FavoriteTag tag){
        getTags().add(tag);
        tag.connectMember(this);
    }
}
