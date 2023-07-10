package project.trendpick_pro.global.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobTestController {

    private final JobConfig jobConfig;

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        try {
            jobConfig.performMakeRecommendProductJob();
        } catch (Exception e) {
            throw new RuntimeException("추천 잡실행 실패");
        }
        return "추천 잡실행 성공";
    }

    @GetMapping("/order")
    @ResponseBody
    public String cancelOrderJob(){
        try {
            jobConfig.performCancelOrderJob();
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException("주문삭제 잡실패");
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException("주문삭제 잡실패");
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException("주문삭제 잡실패");
        } catch (JobRestartException e) {
            throw new RuntimeException("주문삭제 잡실패");
        }
        return "주문삭제 잡성공";
    }
}
