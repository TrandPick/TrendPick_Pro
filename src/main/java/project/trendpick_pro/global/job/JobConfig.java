package project.trendpick_pro.global.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobConfig {
    private final JobLauncher jobLauncher; //job을 실행시키는 주체
    @Qualifier("withParamJob")
    private final Job withParamJob; //Bean에 등록된 job

    @Qualifier("makeRecommendProductJob")
    private final Job makeRecommendProductJob;
    @Qualifier("cancelOrderJob")
    private final Job cancelOrderJob;

    public void performMakeRecommendProductJob() throws Exception {
//        String yearMonth = getPerformMakeRebateDataJobParam1Value(); // 실제 코드

//        String recentlyAccessDate = "2023-06"; // 개발용 코드

        JobParameters param = new JobParametersBuilder()
                .addLocalDateTime("recentlyAccessDate", LocalDateTime.now())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(makeRecommendProductJob, param);
        log.debug(execution.getStatus().toString());
    }

    public void performCancelOrderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters param = new JobParametersBuilder()
                .addLocalDateTime("date", LocalDateTime.now())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(cancelOrderJob, param);
        System.out.println("결과 : " + execution.getStatus());
    }


//    public String getPerformMakeRebateDataJobParam1Value() {
//        LocalDateTime rebateDate = LocalDateTime.now().getDayOfMonth() >= 15 ? LocalDateTime.now().minusMonths(1) : LocalDateTime.now().minusMonths(2);
//
//        return "%04d".formatted(rebateDate.getYear()) + "-" + "%02d".formatted(rebateDate.getMonthValue());
//    }
}
