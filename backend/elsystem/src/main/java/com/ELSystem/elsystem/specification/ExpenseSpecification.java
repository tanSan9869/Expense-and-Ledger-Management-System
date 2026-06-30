package com.ELSystem.elsystem.specification;

import com.ELSystem.elsystem.model.Expense;
import jakarta.persistence.criteria.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {
    private ExpenseSpecification() {}

    public static Specification<Expense> withFilters(
            Long userId,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            String keyword
    ){
        return (root,query,criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by user — mandatory
            predicates.add(
                    criteriaBuilder.equal(root.get("user").get("id"),userId)
            );

            // Only add category filter if provided
            if(categoryId != null){
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"),categoryId));
            }

            // Only add date range if both dates provided
            if(startDate != null && endDate != null){
                predicates.add(criteriaBuilder.between(root.get("expenseDate"),startDate,endDate));
            }else if(startDate != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"),startDate));
            }else if(endDate != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"),endDate));
            }

            // Only add amount range if provided
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"),minAmount));
            }

            if(maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"),maxAmount));
            }

            // Keyword search on description
            if(keyword != null && !keyword.trim().isEmpty()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),"%"+keyword.toLowerCase()+"%"));
            }

            // Avoid duplicate rows when joining — important for pagination
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
