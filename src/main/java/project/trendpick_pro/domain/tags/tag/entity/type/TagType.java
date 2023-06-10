package project.trendpick_pro.domain.tags.tag.entity.type;

public enum TagType { //타입에 따라 다른 태그점수 향상치
    ORDER("ORDER"),
    CART("CART"),
    SHOW("SHOW"),
    REGISTER("REGISTER");

    private String value;

    TagType(String value) {
        this.value = value;
    }
}
