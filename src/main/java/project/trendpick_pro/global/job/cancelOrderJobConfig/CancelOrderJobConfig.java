package project.trendpick_pro.global.job.cancelOrderJobConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.entity.OrderItem;
import project.trendpick_pro.domain.orders.entity.OrderStatus;
import project.trendpick_pro.domain.orders.repository.OrderItemRepository;
import project.trendpick_pro.domain.orders.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CancelOrderJobConfig{
    private final OrderRepository orderRepository;

    @Bean
    public Job cancelOrderJob(Step cancelOrderStep, JobRepository jobRepository) throws Exception {
        return new JobBuilder("cancelOrderJob", jobRepository)
                .start(cancelOrderStep)
//                .incrementer(new RunIdIncrementer()) //계속해서 id 업데이트 (내용이같아도)
                .build();
    }

    @Bean
    @JobScope
    public Step cancelOrderStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Order> orderReader,
            ItemWriter<Order> orderWriter
    ) {
        return new StepBuilder("cancelOrderStep", jobRepository)
                .<Order, Order>chunk(100, transactionManager)
                .reader(orderReader)
                .writer(orderWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Order> orderReader(
            @Value("#{jobParameters['date']}") LocalDateTime date
    ) {

        return new RepositoryItemReaderBuilder<Order>()
                .name("orderReader")
                .repository(orderRepository)
                .methodName("findAllByStatusAndCreatedDateIsBefore")
                .pageSize(100)
                .arguments(Arrays.asList(OrderStatus.TEMP, date))
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<Order> orderWriter() {
        return items -> items.forEach(item -> {
            orderRepository.delete(item);
        });
    }
}
