//package project.trendpick_pro.learning.reddison;
//
//import org.hibernate.Hibernate;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import project.trendpick_pro.IntegrationTestSupport;
//import project.trendpick_pro.domain.brand.entity.Brand;
//import project.trendpick_pro.domain.brand.repository.BrandRepository;
//import project.trendpick_pro.domain.category.entity.MainCategory;
//import project.trendpick_pro.domain.category.entity.SubCategory;
//import project.trendpick_pro.domain.category.repository.MainCategoryRepository;
//import project.trendpick_pro.domain.category.repository.SubCategoryRepository;
//import project.trendpick_pro.domain.common.file.CommonFile;
//import project.trendpick_pro.domain.product.entity.product.Product;
//import project.trendpick_pro.domain.product.entity.productOption.ProductOption;
//import project.trendpick_pro.domain.product.exception.ProductStockOutException;
//import project.trendpick_pro.domain.product.repository.ProductOptionRepository;
//import project.trendpick_pro.domain.product.repository.ProductRepository;
//import project.trendpick_pro.domain.product.service.ProductService;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static project.trendpick_pro.domain.product.entity.product.ProductStatus.SALE;
//
//public class RedissonLearningTest extends IntegrationTestSupport {
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private ProductOptionRepository productOptionRepository;
//
//    @Autowired
//    private MainCategoryRepository mainCategoryRepository;
//
//    @Autowired
//    private SubCategoryRepository subCategoryRepository;
//
//    @Autowired
//    private BrandRepository brandRepository;
//
//    @Autowired
//    private RedissonClient redissonClient;
//
//    @AfterEach
//    void tearDown() {
//        productRepository.deleteAllInBatch();
//        productOptionRepository.deleteAllInBatch();
//        subCategoryRepository.deleteAllInBatch();
//        mainCategoryRepository.deleteAllInBatch();
//        brandRepository.deleteAllInBatch();
//    }
//
//    @DisplayName("상품의 재고 차감시, 분산락 미적용 시 재고가 중복 차감 된다.")
//    @Test
//    void decreaseStockTestWithOutDistributedLock() throws Exception {
//        //given
//        int numberOfThreads = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        Product product = settingProduct();
//
//        //when
//        for (int i = 0; i < numberOfThreads; i++) {
//            executorService.submit(() -> {
//                try {
//                    productService.decreaseStock(product.getId(), 1);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        //then
//        Product findProduct = productService.findByIdInTest(product.getId());
//        Hibernate.initialize(findProduct.getProductOption());
//        assertThat(findProduct.getProductOption().getStock()).isNotZero();
//    }
//
//    @DisplayName("상품의 재고 차감시, Redisson 분산락에 의해 재고가 중복 차감되지 않는다.")
//    @Test
//    void decreaseStockTest() throws InterruptedException {
//        //given
//        Product product = settingProduct();
//
//        //when
//        int numberOfThreads = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        for (int i = 0; i < numberOfThreads; i++) {
//            executorService.submit(() -> {
//                try {
//                    RLock lock = getDistributedLock(product.getProductCode());
//                    try {
//                        if (lock.tryLock(3, 3, TimeUnit.SECONDS)) {
//                            try {
//                                productService.decreaseStock(product.getId(), 1);
//                            } catch (ProductStockOutException e) {
//                                throw new RuntimeException(e);
//                            } finally {
//                                lock.unlock();
//                            }
//                        } else {
//                            throw new RedisLockException("해당 락이 사용중 입니다");
//                        }
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        //then
//        Product findProduct = productService.findByIdInTest(product.getId());
//        assertThat(findProduct.getProductOption().getStock()).isZero();
//    }
//
//    private RLock getDistributedLock(String key) {
//        return redissonClient.getLock("ORD_" + key);
//    }
//
//    private Product settingProduct() {
//        CommonFile commonFile = CommonFile.builder()
//                .fileName("test.jpg")
//                .build();
//
//        MainCategory mainCategory = mainCategoryRepository.save(new MainCategory("shirt"));
//        SubCategory subCategory = subCategoryRepository.save(new SubCategory("long shirt", mainCategory));
//        Brand polo = brandRepository.save( new Brand("Polo"));
//
//        ProductOption productOption = ProductOption.builder()
//                .size(List.of("L", "XL"))
//                .color(List.of("red", "blue"))
//                .stock(100)
//                .price(10000)
//                .build();
//
//        productOption.settingConnection(
//                polo,
//                mainCategory,
//                subCategory,
//                commonFile,
//                SALE
//        );
//
//        Product product = Product.builder()
//                .productCode("P" + UUID.randomUUID())
//                .title("product1 title")
//                .description("product1 description")
//                .build();
//        product.connectProductOption(productOption);
//        return productRepository.save(product);
//    }
//}
