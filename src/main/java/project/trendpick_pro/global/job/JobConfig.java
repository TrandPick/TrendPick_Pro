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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobConfig {
    private final JobLauncher jobLauncher; //job을 실행시키는 주체
    @Qualifier("makeRecommendProductJob")
    private final Job makeRecommendProductJob;
    @Qualifier("cancelOrderJob")
    private final Job cancelOrderJob;


    @Scheduled(cron = "0 0 3 * * *")
    public void performMakeRecommendProductJob() throws Exception {

        LocalDateTime fromDate = LocalDateTime.now().minusDays(1).with(LocalTime.MIN); //전날 00:00
        LocalDateTime toDate = LocalDateTime.now().minusDays(1).with(LocalTime.MAX); //전날 11:59

        fromDate = LocalDateTime.now().minusDays(2); //테스트용
        toDate = LocalDateTime.now().plusDays(2); //테스트용

        JobParameters param = new JobParametersBuilder()
                .addLocalDateTime("fromDate", fromDate)
                .addLocalDateTime("toDate", toDate)
                .toJobParameters();

        JobExecution execution = jobLauncher.run(makeRecommendProductJob, param);
        log.debug(execution.getStatus().toString());
    }


    @Scheduled(cron = "0 0 1 * * *")
    public void performCancelOrderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//        LocalDateTime real = LocalDateTime.now().minusDays(1).with(LocalTime.MAX); //전날 11:59
        LocalDateTime fake = LocalDateTime.now().plusDays(1); //테스트용

        JobParameters param = new JobParametersBuilder()
                .addLocalDateTime("date", fake) //전날 11:59
                .toJobParameters();
        JobExecution execution = jobLauncher.run(cancelOrderJob, param);
        System.out.println("결과 : " + execution.getStatus());
    }
}
