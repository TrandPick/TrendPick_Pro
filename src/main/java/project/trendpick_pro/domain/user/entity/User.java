package project.trendpick_pro.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.trendpick_pro.domain.tag.entity.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String password;

    private String name;

    private String phone_num;

    private Date birth;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    //@OneToMany(mappedBy = "user")
    private Long tag_id;    //private List<Tag> tags= new ArrayList<>(); Tag가 N쪽인데 연관관계를 어떻게 잡아야할까용...Tag가 User를 가지고 있을 필요는 없을 것 같은데 쓰읍
    private String bank_name;
    private Long account;


}
