package project.trendpick_pro.domain.tags.favoritetag.service;

import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.tags.type.TagType;

public interface FavoriteTagService {
    void updateTag(Member member, Product product, TagType type);

}
