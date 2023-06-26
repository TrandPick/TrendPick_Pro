package project.trendpick_pro.domain.tags.favoritetag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.tags.favoritetag.entity.FavoriteTag;
import project.trendpick_pro.domain.tags.favoritetag.repository.FavoriteTagRepository;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.product.Product;
import project.trendpick_pro.domain.tags.tag.entity.Tag;
import project.trendpick_pro.domain.tags.tag.entity.type.TagType;
import project.trendpick_pro.domain.tags.tag.repository.TagRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteTagService {
    private final FavoriteTagRepository favoriteTagRepository;
    private final TagRepository tagRepository;

    public Set<FavoriteTag> getAllTags(Member member) {
        List<FavoriteTag> list = favoriteTagRepository.findAll();
        return Set.copyOf(list);
    }

    @Transactional
    public void updateTag(Member member, Product product, TagType type) {
        List<Tag> tagList = tagRepository.findAllByProduct(product);
        List<FavoriteTag> tags = favoriteTagRepository.findAllByMember(member);

        for(Tag tagByProduct : tagList){
            boolean hasTag = false;
            for(FavoriteTag tagByMember : tags){
                if(tagByProduct.getName().equals(tagByMember.getName())){ //기존에 가지고 있던 태그에는 점수 부여
                    tagByMember.increaseScore(type);
                    hasTag = true;
                    break;
                }
            }
            if(!hasTag){ //태그를 가지고 있지 않다면 추가해준다.
                FavoriteTag favoriteTag = new FavoriteTag(tagByProduct.getName());
                favoriteTag.increaseScore(type);
                member.addTag(favoriteTag);
                favoriteTagRepository.save(favoriteTag);
            }
        }
    }
}
