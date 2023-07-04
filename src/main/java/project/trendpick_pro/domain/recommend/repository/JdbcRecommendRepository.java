package project.trendpick_pro.domain.recommend.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.recommend.entity.Recommend;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcRecommendRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsert(List<Recommend> recommends) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO recommend(product_id, member_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, recommends.get(i).getProduct().getId());
                        ps.setLong(2, recommends.get(i).getMember().getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return recommends.size();
                    }
                }
        );
    }

}
