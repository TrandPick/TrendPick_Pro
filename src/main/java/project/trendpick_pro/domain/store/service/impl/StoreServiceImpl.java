package project.trendpick_pro.domain.store.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;
import project.trendpick_pro.domain.store.service.StoreService;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public Store save(Store store) {
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
    public void addRebateCash(Store store, long price) {
        store.addRebatedCash(price);
        storeRepository.save(store);
    }

    @Override
    public int getRestCash(String storeName) {
        return findByBrand(storeName).getRebatedCash();
    }

    @Override
    @Transactional
    public void saveAll(List<String> names) {
        List<Store> list = new ArrayList<>();
        for (String name : names) {
            list.add(new Store(name));
        }
        storeRepository.saveAll(list);
    }
}
