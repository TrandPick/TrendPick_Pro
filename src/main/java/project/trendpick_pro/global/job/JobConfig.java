package project.trendpick_pro.global.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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

    @Qualifier("makeRebateDataJob")
    private final Job makeRebateDataJob;


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

    @Scheduled(cron = "0 0 4 * * *") // 실제 코드
    // @Scheduled(cron = "30 * * * * *") // 개발용
    public void performMakeRebateDataJob() throws Exception {
        String yearMonth = getPerformMakeRebateDataJobParam1Value(); // 실제 코드
        //   String yearMonth = "2023-07"; // 개발용

        JobParameters param = new JobParametersBuilder()
                .addString("yearMonth", yearMonth)
                .toJobParameters();
        JobExecution execution = jobLauncher.run(makeRebateDataJob, param);

        System.out.println(execution.getStatus());
    }

    public String getPerformMakeRebateDataJobParam1Value() {
        LocalDateTime rebateDate = LocalDateTime.now().getDayOfMonth() >= 15 ? LocalDateTime.now().minusMonths(1) : LocalDateTime.now().minusMonths(2);

        return "%04d".formatted(rebateDate.getYear()) + "-" + "%02d".formatted(rebateDate.getMonthValue());
    }
}
