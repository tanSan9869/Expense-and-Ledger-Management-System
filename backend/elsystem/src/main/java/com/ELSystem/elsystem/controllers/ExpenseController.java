package com.ELSystem.elsystem.controllers;

import com.ELSystem.elsystem.dto.request.ExpenseFilterRequest;
import com.ELSystem.elsystem.dto.request.ExpenseRequest;
import com.ELSystem.elsystem.dto.response.CategorySummaryResponse;
import com.ELSystem.elsystem.dto.response.ExpenseResponse;
import com.ELSystem.elsystem.dto.response.PagedResponse;
import com.ELSystem.elsystem.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@RequestBody @Valid ExpenseRequest request){
        return new ResponseEntity<>(expenseService.createExpense(request), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<ExpenseResponse>> getByUser(@PathVariable Long userId, @ModelAttribute ExpenseFilterRequest filter){
        return new ResponseEntity<>(expenseService.getFilteredExpenses(userId,filter),HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<List<CategorySummaryResponse>> getCategorySummary(@PathVariable Long userId){
        return new ResponseEntity<>(expenseService.getCategorySummaries(userId),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getById(@PathVariable Long id){
        return new ResponseEntity<>(expenseService.getExpenseById(id),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id,@RequestBody @Valid ExpenseRequest request){
        return new ResponseEntity<>(expenseService.updateExpense(id,request),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        expenseService.deleteExpense(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
