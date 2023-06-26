//package project.trendpick_pro.global.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.erhlc.AbstractElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.client.erhlc.RestClients;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "project/trendpick_pro/global/search/repository")
//public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
//
//    @Value("${spring.elasticsearch.rest.uris}")
//    private String elasticsearchUrl;
//
//    @Override
//    @Bean
//    public RestHighLevelClient elasticsearchClient() {
//        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo(elasticsearchUrl)
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }
//}