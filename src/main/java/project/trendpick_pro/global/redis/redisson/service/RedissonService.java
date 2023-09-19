package project.trendpick_pro.global.redis.redisson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.orders.entity.Order;
import project.trendpick_pro.domain.orders.service.OrderService;
import project.trendpick_pro.global.kafka.outbox.entity.OrderMaterial;
import project.trendpick_pro.global.kafka.outbox.service.OutboxMessageService;
import project.trendpick_pro.global.redis.exception.RedisLockException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonService {
    private final RedissonClient redissonClient;
    private final OrderService orderService;
    private final OutboxMessageService outboxMessageService;

    public void processOrderWithLock(String orderId, String orderMaterialCode) throws InterruptedException {
        Order order = orderService.findById(Long.valueOf(orderId));
        List<OrderMaterial> orderMaterials = outboxMessageService.findOrderMaterial(orderMaterialCode);
        List<RLock> locks = getLocks(orderMaterials); //주문 상품들에 대한 락
        if (isCompleteLock(locks))
            try{
                orderService.tryOrder(orderId, orderMaterials);
            } catch (Exception e){
                log.error("Error processing order", e);
            } finally {
                try {
                    unlock(locks);
                } catch (IllegalMonitorStateException e) {
                    log.info("Redisson Lock Already UnLocked");
                }
            }
        else throw new RedisLockException("해당 락이 사용중입니다");
    }

    private List<RLock> getLocks(List<OrderMaterial> orderMaterials) {
        List<RLock> lockList = new ArrayList<>();
        for(OrderMaterial orderMaterial : orderMaterials)
            lockList.add(getDistributedLock(String.valueOf(orderMaterial.getProductId())));
        return lockList;
    }

    private boolean isCompleteLock(List<RLock> lockList) throws InterruptedException {
        for (RLock lock : lockList) {
            if(!lock.tryLock(3, 1, TimeUnit.SECONDS)){
               return false;
            }
        }
        return true;
    }

    public void unlock(List<RLock> lockList){
        for (RLock lock : lockList){
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLocked");
            }
        }
    }

    private RLock getDistributedLock(String key) {
        return redissonClient.getLock("PRODUCT_" + key);
    }
}
