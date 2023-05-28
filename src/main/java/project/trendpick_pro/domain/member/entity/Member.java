package project.trendpick_pro.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String username;

    private String phone_num;

    private Date birth;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    //@OneToMany(mappedBy = "user")
    private Long tag_id;    //private List<Tag> tags= new ArrayList<>(); Tag가 N쪽인데 연관관계를 어떻게 잡아야할까용...Tag가 User를 가지고 있을 필요는 없을 것 같은데 쓰읍
    private String bank_name;
    private Long account;


}
