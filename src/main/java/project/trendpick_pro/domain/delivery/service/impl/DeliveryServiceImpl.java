package project.trendpick_pro.domain.delivery.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.delivery.repository.DeliveryRepository;
import project.trendpick_pro.domain.delivery.service.DeliveryService;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public void updateDeliveryStatus(Long deliveryId, String status) {
        deliveryRepository.findById(deliveryId)
                .ifPresent(delivery -> delivery.updateStatus(status));
    }
}
