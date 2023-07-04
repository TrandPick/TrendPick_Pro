package project.trendpick_pro.global.job;

import jdk.jfr.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import project.trendpick_pro.global.job.JobConfig;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final JobConfig jobConfig;

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        try {
            jobConfig.performMakeRecommendProductJob();
        } catch (Exception e) {
            throw new RuntimeException("Job 실행 실패");
        }
        return "잡실행 성공";
    }

    @GetMapping("/order")
    @ResponseBody
    public String cancelOrderJob(){
        try {
            jobConfig.performCancelOrderJob();
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }
        return "성공";
    }
}
