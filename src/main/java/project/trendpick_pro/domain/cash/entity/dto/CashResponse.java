package project.trendpick_pro.domain.cash.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.trendpick_pro.domain.cash.entity.CashLog;

@Getter
@AllArgsConstructor
public class CashResponse {
    CashLog cashLog;
}
