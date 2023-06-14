package project.trendpick_pro.global.basedata.tagname.entity.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagNameResponse {

    private String name;

    public TagNameResponse(String name) {
        this.name = name;
    }
}
