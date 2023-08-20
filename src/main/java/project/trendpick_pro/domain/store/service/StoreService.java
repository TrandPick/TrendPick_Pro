package project.trendpick_pro.domain.store.service;

import project.trendpick_pro.domain.store.entity.Store;

public interface StoreService {
    Store save(Store store);
    Store findByBrand(String storeName);
    void addRebateCash(String storeName, int calculateRebatePrice);
    int getRestCash(String storeName);
}
