package project.trendpick_pro.domain.tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.trendpick_pro.domain.FavoriteTag;
import project.trendpick_pro.domain.member.entity.Member;
import project.trendpick_pro.domain.product.entity.Product;
import project.trendpick_pro.domain.tag.entity.Tag;
import project.trendpick_pro.domain.tag.entity.dto.response.TagListResponse;
import project.trendpick_pro.domain.tag.entity.dto.response.TagResponse;
import project.trendpick_pro.domain.tag.entity.type.TagType;
import project.trendpick_pro.domain.tag.repository.TagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public void save(String name) {
        tagRepository.save(new Tag(name));
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional
    public void updateTag(Member member, Product product, TagType type) {
        List<Tag> tagList = product.getTags();
        List<FavoriteTag> tags = member.getTags();

        //장바구니는 2유형이라고 가정
        for(Tag tagByProduct : tagList){
            boolean hasTag = false;
            for(FavoriteTag tagByMember : tags){
                if(tagByProduct.getName().equals(tagByMember.getName())){ //기존에 가지고 있던 태그에는 점수 부여
                    tagByMember.increaseScore(type);
                    hasTag = true;
                    break;
                }
            }
            if(!hasTag) //태그를 가지고 있지 않다면 추가해준다.
                tags.add(new FavoriteTag(tagByProduct, tagByProduct.getName()));
        }
    }
}
