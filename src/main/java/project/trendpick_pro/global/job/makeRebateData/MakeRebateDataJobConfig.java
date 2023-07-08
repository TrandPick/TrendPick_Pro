package project.trendpick_pro.global.job.makeRebateData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.domain.rebate.entity.RebateOrderItem;
import project.trendpick_pro.domain.rebate.repository.RebateOrderItemRepository;
import project.trendpick_pro.global.util.Ut;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MakeRebateDataJobConfig {
    private final OrderItemRepository orderItemRepository;
    private final RebateOrderItemRepository rebateOrderItemRepository;
    private final JobRepository jobRepository;

    @Bean
    public Job makeRebateDataJob(Step makeRebateDataStep1) {
        return new JobBuilder("makeRebateDataJob", jobRepository)
                .start(makeRebateDataStep1)
                .build();
    }

    @Bean
    @JobScope
    public Step makeRebateDataStep1(
            PlatformTransactionManager transactionManager,
            ItemReader<OrderItem> orderItemReader,
            ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor,
            ItemWriter<RebateOrderItem> rebateOrderItemWriter
    ) {
        return new StepBuilder("makeRebateDataStep1", jobRepository)
                .<OrderItem, RebateOrderItem>chunk(100, transactionManager)
                .reader(orderItemReader)
                .processor(orderItemToRebateOrderItemProcessor)
                .writer(rebateOrderItemWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<OrderItem> orderItemReader(
            @Value("#{jobParameters['yearMonth']}") String yearMonth
    ) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);
        LocalDateTime fromDate = Ut.date.parse(yearMonth + "-01 00:00:00.000000");
        LocalDateTime toDate = Ut.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));

        return new RepositoryItemReaderBuilder<OrderItem>()
                .name("orderItemReader")
                .repository(orderItemRepository)
                .methodName("findAllByPayDateBetween")
                .pageSize(100)
                .arguments(Arrays.asList(fromDate, toDate))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor() {
        return RebateOrderItem::new;
    }

    @StepScope
    @Bean
    public ItemWriter<RebateOrderItem> rebateOrderItemWriter() {
        return items -> items.forEach(item -> {
            RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

            if (oldRebateOrderItem != null) {
                if (oldRebateOrderItem.isRebateDone()) {
                    return;
                }

                rebateOrderItemRepository.delete(oldRebateOrderItem);
            }

            rebateOrderItemRepository.save(item);
        });
    }
}