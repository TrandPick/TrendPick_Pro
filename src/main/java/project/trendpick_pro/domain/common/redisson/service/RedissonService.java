package project.trendpick_pro.domain.common.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.redis.exception.RedisLockException;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonService {

    private final RedissonClient redissonClient;
    private final OrderService orderService;

    public void processOrderWithLock(String orderKey) throws InterruptedException {
        RLock lock = getDistributedLock(orderKey);
        if (lock.tryLock(3, 3, TimeUnit.SECONDS)) {
            try {
                log.info(lock.getName());
                orderService.tryOrder(orderKey);
            } catch (Exception e) {
                log.error("Error processing order", e);
            } finally {;
                try {
                    lock.unlock();
                } catch (IllegalMonitorStateException e) {
                    log.info("Redisson Lock Already UnLocked");
                }
            }
        } else {
            throw new RedisLockException("해당 락이 사용중입니다");
        }
    }

    private RLock getDistributedLock(String key) {
        return redissonClient.getLock("ORD_" + key);
    }
}
