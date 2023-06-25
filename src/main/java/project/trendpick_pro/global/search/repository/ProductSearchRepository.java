//package project.trendpick_pro.global.search.repository;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.stereotype.Repository;
//import project.trendpick_pro.global.search.entity.ProductSearch;
//
//@Repository
//public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearch, String> {
//    Page<ProductSearch> findByNameContainingOrBrandContaining(String nameKeyword, String brandKeyword, Pageable pageable);
//}
