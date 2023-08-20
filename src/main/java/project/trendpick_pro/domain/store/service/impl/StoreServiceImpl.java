package project.trendpick_pro.domain.store.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;
import project.trendpick_pro.domain.store.service.StoreService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public Store save(Store store){
        return storeRepository.save(store);
    }

    @Override
    public Store findByBrand(String storeName) {
        return storeRepository.findByBrand(storeName).orElseThrow(
                () -> new IllegalArgumentException("해당 스토어는 존재하지 않는 스토어입니다.")
        );
    }

    @Override
    @Transactional
    public void addRebateCash(String storeName, int calculateRebatePrice) {
        Store store = findByBrand(storeName);
        store.addRebatedCash(calculateRebatePrice);
    }

    @Override
    public int getRestCash(String storeName) {
        return findByBrand(storeName).getRebatedCash();
    }
}
