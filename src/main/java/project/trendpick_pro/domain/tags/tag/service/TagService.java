package project.trendpick_pro.domain.tags.tag.service;

import project.trendpick_pro.domain.tags.tag.entity.Tag;

import java.util.Set;

public interface TagService {
    void delete(Set<Tag> tags);
}
