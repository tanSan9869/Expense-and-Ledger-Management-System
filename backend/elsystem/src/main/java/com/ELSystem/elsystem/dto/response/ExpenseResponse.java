package com.ELSystem.elsystem.dto.response;
// ExpenseResponse.java  — what your API returns

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseResponse {
    private Long id;
    private Long categoryId;
    private String username;
    private String categoryName;
    private BigDecimal amount;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;
}
