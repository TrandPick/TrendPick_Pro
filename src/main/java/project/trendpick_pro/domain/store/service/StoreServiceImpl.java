package project.trendpick_pro.domain.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.store.entity.Store;
import project.trendpick_pro.domain.store.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService{
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public Store save(Store store){
        return storeRepository.save(store);
    }

    @Override
    @Transactional(readOnly = true)
    public Store findByBrand(String storeName) {
        return storeRepository.findByBrand(storeName).orElseThrow(
                () -> new IllegalArgumentException("해당 스토어는 존재하지 않는 스토어입니다.")
        );
    }
}
