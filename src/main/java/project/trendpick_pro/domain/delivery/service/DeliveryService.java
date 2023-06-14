package project.trendpick_pro.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.trendpick_pro.domain.delivery.repository.DeliveryRepository;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public void updateDeliveryStatus(Long deliveryId, String status) {
//        deliveryRepository.updateDeliveryStatus(deliveryId, status);
    }

    
}
