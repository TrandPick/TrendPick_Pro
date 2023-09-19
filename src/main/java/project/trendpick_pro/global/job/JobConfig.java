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


    //매일 03시에 전날에 접속 이력이 있는 회원을 대상으로 추천 상품을 갱신해준다.
    @Scheduled(cron = "0 0 3 * * *")
    public void performMakeRecommendProductJob() throws Exception {

        LocalDateTime fromDate = LocalDateTime.now().minusDays(1).with(LocalTime.MIN); //전날 00:00
        LocalDateTime toDate = LocalDateTime.now().minusDays(1).with(LocalTime.MAX); //전날 11:59
//        fromDate = LocalDateTime.now().minusDays(2); //테스트용
//        toDate = LocalDateTime.now().plusDays(2); //테스트용

        JobParameters param = new JobParametersBuilder()
                .addLocalDateTime("fromDate", fromDate)
                .addLocalDateTime("toDate", toDate)
                .toJobParameters();

        JobExecution execution = jobLauncher.run(makeRecommendProductJob, param);
        log.info(execution.getStatus().toString());
    }

    // 매일 02시에 전날 생성된 주문 객체중 결제 처리가 안된 객체를 일괄 삭제 처리한다.
    @Scheduled(cron = "0 0 2 * * *")
    public void performCancelOrderJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        LocalDateTime date = LocalDateTime.now().minusDays(1).with(LocalTime.MAX); //전날 11:59
//        LocalDateTime fake = LocalDateTime.now().plusDays(1); //테스트용

        JobParameters param = new JobParametersBuilder()
                .addLocalDateTime("date", date) //전날 11:59
                .toJobParameters();
        JobExecution execution = jobLauncher.run(cancelOrderJob, param);
        log.info("job result : " + execution.getStatus());

    }

    // 매일 04시에 일일정산을 통해 월 정산을 갱신해준다.
    @Scheduled(cron = "0 0 4 * * *") // 실제 코드
    public void performMakeRebateDataJob() throws Exception {
        String yearMonth = getPerformMakeRebateDataJobParam1Value(); // 실제 코드
        //   String yearMonth = "2023-07"; // 개발용

        JobParameters param = new JobParametersBuilder()
                .addString("yearMonth", yearMonth)
                .toJobParameters();
        JobExecution execution = jobLauncher.run(makeRebateDataJob, param);

        log.info(String.valueOf(execution.getStatus()));
    }

    public String getPerformMakeRebateDataJobParam1Value() {
        LocalDateTime rebateDate = LocalDateTime.now().getDayOfMonth() >= 15 ? LocalDateTime.now().minusMonths(1) : LocalDateTime.now().minusMonths(2);

        return "%04d".formatted(rebateDate.getYear()) + "-" + "%02d".formatted(rebateDate.getMonthValue());
    }
}
