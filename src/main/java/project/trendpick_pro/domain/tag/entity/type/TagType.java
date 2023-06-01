package project.trendpick_pro.domain.tag.entity.type;

public enum TagType { //타입에 따라 다른 태그점수 향상치
    ORDER("ORDER"),
    CART("CART"),
    SHOW("SHOW");

    private String value;

    TagType(String value) {
        this.value = value;
    }
}
