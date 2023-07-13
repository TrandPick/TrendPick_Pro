package project.trendpick_pro.domain.tags.favoritetag.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.favoritetag.repository.FavoriteTagRepository;
import project.trendpick_pro.domain.tags.favoritetag.service.FavoriteTagService;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;
import project.trendpick_pro.domain.tags.tag.entity.TagType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteTagServiceImpl implements FavoriteTagService {
    private final FavoriteTagRepository favoriteTagRepository;
    private final TagRepository tagRepository;

    @Async
    @Transactional
    @Override
    public void updateTag(Member member, Product product, TagType type) {
        List<Tag> tagList = tagRepository.findAllByProduct(product);
        List<FavoriteTag> tags = favoriteTagRepository.findAllByMember(member);

        for(Tag tagByProduct : tagList){
            boolean hasTag = false;
            for(FavoriteTag tagByMember : tags){
                if(tagByProduct.getName().equals(tagByMember.getName())){
                    tagByMember.increaseScore(type);
                    hasTag = true;
                    break;
                }
            }
            if(!hasTag){
                FavoriteTag favoriteTag = new FavoriteTag(tagByProduct.getName());
                favoriteTag.increaseScore(type);
                member.addTag(favoriteTag);
                favoriteTagRepository.save(favoriteTag);
            }
        }
    }
}
