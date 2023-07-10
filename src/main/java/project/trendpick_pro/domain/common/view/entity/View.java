package project.trendpick_pro.domain.common.view.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class View {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long count = 0L;

    public void increment() {
        this.count++;
    }
}
