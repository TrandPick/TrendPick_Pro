package project.trendpick_pro.global.basedata.tagname.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class TagName {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public TagName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TagName{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
