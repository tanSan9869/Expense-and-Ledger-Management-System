package com.ELSystem.elsystem.dto.request;
// ExpenseRequest.java  — what the frontend sends
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01",message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2,message = "Amount must have 2 decimal places")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255,message = "Description must be less than 255 characters")
    private String description;

    @NotNull(message = "Expense date is required")
    @PastOrPresent(message = "Expense date must be in the past or present")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;
}
