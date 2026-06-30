package com.ELSystem.elsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategorySummaryResponse {
    private String categoryName;
    private BigDecimal totalAmount;
}
