package com.ELSystem.elsystem.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseFilterRequest {
    private Long categoryId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    // Pagination params with sensible defaults
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String keyword;

    private int page=0;
    private int size=10;
    private String sortBy = "expenseDate";
    private String sortDir = "desc";

}
