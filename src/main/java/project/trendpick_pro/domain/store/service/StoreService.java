package project.trendpick_pro.domain.store.service;

import project.trendpick_pro.domain.store.entity.Store;

import java.util.List;

public interface StoreService {
    Store save(Store store);
    Store findByBrand(String storeName);
    void addRebateCash(Store store, long price);
    int getRestCash(String storeName);

    void saveAll(List<String> brands);
}
