package com.ELSystem.elsystem.service;

import com.ELSystem.elsystem.dto.response.LedgerResponse;
import com.ELSystem.elsystem.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerEntryRepository ledgerEntryRepository;

    public BigDecimal getBalance(Long userId){
        return ledgerEntryRepository.calculateBalanceByUserId(userId);
    }

    public List<LedgerResponse> getLedgerEntries(Long userId){
        return ledgerEntryRepository.findByUserIdOrderByEntryDateDesc(userId)
                .stream()
                .map(entry-> LedgerResponse.builder()
                        .id(entry.getId())
                        .entryDate(entry.getEntryDate())
                        .entryType(entry.getEntryType())
                        .debitAmount(entry.getDebitAmount())
                        .creditAmount(entry.getCreditAmount())
                        .runningBalance(entry.getRunningBalance())
                        .expenseDescription(entry.getExpense().getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
