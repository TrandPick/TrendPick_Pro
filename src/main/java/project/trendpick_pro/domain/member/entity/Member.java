package project.trendpick_pro.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import project.trendpick_pro.domain.brand.entity.Brand;
import project.trendpick_pro.domain.cart.entity.Cart;
import project.trendpick_pro.domain.common.base.BaseTimeEntity;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "index_member",
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
    private RoleType role;

    private String brand;

    @OneToOne(mappedBy = "member")
    private Cart cart;
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavoriteTag> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Order> Orders = new ArrayList<>();

    private String bankName;
    private String bankAccount;
    private String address;
    private LocalDateTime recentlyAccessDate;
    private long restCash;
    @Builder
    public Member(String email, String password, String username, String phoneNumber, RoleType role,long restCash) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.restCash=restCash;
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

    public void changeTags(Set<FavoriteTag> tags) {
        this.tags = tags;
        for(FavoriteTag tag : tags){
            tag.connectMember(this);
            tag.increaseScore(TagType.REGISTER);
        }
    }

    //양방향 메서드
    public void addTag(FavoriteTag tag){
        getTags().add(tag);
        tag.connectMember(this);
    }

    public void updateRecentlyAccessDate() {
        this.recentlyAccessDate = LocalDateTime.now();
    }

}
