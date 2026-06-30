package com.ELSystem.elsystem.service;

import com.ELSystem.elsystem.dto.request.ExpenseFilterRequest;
import com.ELSystem.elsystem.dto.request.ExpenseRequest;
import com.ELSystem.elsystem.dto.response.CategorySummaryResponse;
import com.ELSystem.elsystem.dto.response.ExpenseResponse;
import com.ELSystem.elsystem.dto.response.PagedResponse;
import com.ELSystem.elsystem.exception.InvalidTransactionException;
import com.ELSystem.elsystem.exception.ResourceNotFoundException;
import com.ELSystem.elsystem.model.*;
import com.ELSystem.elsystem.repository.*;
import com.ELSystem.elsystem.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionLogRepository transactionLogRepository;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        // 1. Save the expense

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .build();

        expense = expenseRepository.save(expense);

        // 2. Compute a new running balance
        BigDecimal currentBalance = ledgerEntryRepository.calculateBalanceByUserId(user.getId());
        BigDecimal newBalance = currentBalance.subtract(request.getAmount());

        if(newBalance.compareTo(BigDecimal.valueOf(-100000))<0){
            throw new InvalidTransactionException("Transaction would exceed the maximum overdraft limit");
        }

        // 3. Create a ledger entry
        LedgerEntry entry = LedgerEntry.builder()
                .user(user)
                .expense(expense)
                .entryType(LedgerEntry.EntryType.DEBIT)
                .debitAmount(request.getAmount())
                .creditAmount(BigDecimal.ZERO)
                .runningBalance(newBalance)
                .build();
        ledgerEntryRepository.save(entry);

        // 4. Log the transaction
        TransactionLog log = TransactionLog.builder()
                .expense(expense)
                .action("CREATED")
                .details("Expense of " + request.getAmount() + "created")
                .build();
        transactionLogRepository.save(log);

        return mapToResponse(expense);
    }

    public List<ExpenseResponse> getExpensesByUser(Long userId){
        return expenseRepository.findAllByUserIdWithDetails(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(Long expenseId){
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        return mapToResponse(expense);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request){
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));

        BigDecimal oldAmount = expense.getAmount();
        BigDecimal newAmount = request.getAmount();

        expense.setAmount(newAmount);
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(category);
        expenseRepository.save(expense);

        LedgerEntry entry = ledgerEntryRepository.findByExpenseId(id).orElseThrow();

        BigDecimal difference = newAmount.subtract(oldAmount);

        entry.setDebitAmount(difference);

        BigDecimal updatedBalance = entry.getRunningBalance().subtract(difference);
        entry.setRunningBalance(updatedBalance);
        ledgerEntryRepository.save(entry);

        recalculateSubsequentEntries(expense.getUser().getId(),entry.getEntryDate(),difference);

        TransactionLog log = TransactionLog.builder()
                .expense(expense)
                .action("UPDATED")
                .details("Expense updated to amount " + request.getAmount())
                .build();
        transactionLogRepository.save(log);

        return mapToResponse(expense);
    }

    private void recalculateSubsequentEntries(Long id, LocalDate afterDate, BigDecimal difference) {
        List<LedgerEntry> subsequentEntries = ledgerEntryRepository.findByUserIdAfterDate(id, afterDate.atStartOfDay());

        for(LedgerEntry subsequent : subsequentEntries){
            subsequent.setRunningBalance(subsequent.getRunningBalance().subtract(difference));
            ledgerEntryRepository.save(subsequent);
        }
    }

    @Transactional
    public void deleteExpense(Long id){
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        BigDecimal deletedAmount = expense.getAmount();
        Long userId = expense.getUser().getId();

        LedgerEntry entry = ledgerEntryRepository.findByExpenseId(id).orElseThrow();

        LocalDate entryDate = entry.getEntryDate();

        TransactionLog log = TransactionLog.builder()
                .expense(expense)
                .action("DELETED")
                .details("Expense of "+ expense.getAmount() +" deleted")
                .build();
        transactionLogRepository.save(log);

        ledgerEntryRepository.delete(entry);

        expenseRepository.delete(expense);

        recalculateSubsequentEntries(userId,entryDate,deletedAmount.negate());
    }

    public PagedResponse<ExpenseResponse> getFilteredExpenses(Long userId, ExpenseFilterRequest filter){
        // Build sort direction
        Sort.Direction direction = filter.getSortDir().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        // Build pageable — page number, size, sort
        Pageable pageable = PageRequest.of(filter.getPage(),filter.getSize(),Sort.by(direction,filter.getSortBy()));

        // Build the dynamic specification from filter params
        Specification<Expense> spec = ExpenseSpecification.withFilters(
                userId,
                filter.getCategoryId(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getMinAmount(),
                filter.getMaxAmount(),
                filter.getKeyword()
        );

        // Single DB call — filtering, sorting, pagination all in SQL
        Page<Expense> expensePage = expenseRepository.findAll(spec,pageable);

        // Map entity page to response DTO page
        List<ExpenseResponse> content = expensePage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<ExpenseResponse>builder()
                .content(content)
                .page(expensePage.getNumber())
                .size(expensePage.getSize())
                .totalElements(expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .last(expensePage.isLast())
                .first(expensePage.isFirst())
                .build();
    }

    // Category summary for dashboard
    public List<CategorySummaryResponse> getCategorySummaries(Long userId){
        return expenseRepository.getTotalSpentPerCategory(userId)
                .stream()
                .map(row -> CategorySummaryResponse.builder()
                        .categoryName((String) row[0])
                        .totalAmount((BigDecimal) row[1])
                        .build())
                .toList();
    }

    // Maps entity → response DTO
    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .categoryId(expense.getCategory().getId())
                .username(expense.getUser().getUsername())
                .categoryName(expense.getCategory().getName())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .build();
    }
}

