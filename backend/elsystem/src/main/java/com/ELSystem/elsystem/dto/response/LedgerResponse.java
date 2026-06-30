package com.ELSystem.elsystem.dto.response;

import com.ELSystem.elsystem.model.LedgerEntry;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class LedgerResponse {
    private Long id;
    private LedgerEntry.EntryType entryType;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private BigDecimal runningBalance;
    private LocalDate entryDate;
    private String expenseDescription;
}
