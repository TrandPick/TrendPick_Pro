package project.trendpick_pro.domain.ask.entity;

import lombok.Getter;

@Getter
public enum AskStatus {
    YET("YET"),
    COMPLETED("COMPLETED");

    private String value;

    AskStatus(String value) {
        this.value = value;
    }
}
