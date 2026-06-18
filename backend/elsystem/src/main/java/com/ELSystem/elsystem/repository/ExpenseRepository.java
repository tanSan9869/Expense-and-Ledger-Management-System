package com.ELSystem.elsystem.repository;

import com.ELSystem.elsystem.model.Expense;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense,Long>, JpaSpecificationExecutor<Expense> {
    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdAndCategoryId(Long userId,Long categoryId);

    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // Avoids N+1 — fetches user and category in one JOIN query
    @Query("SELECT e FROM Expense e JOIN FETCH e.user JOIN FETCH e.category WHERE e.user.id = :userId")
    List<Expense> findAllByUserIdWithDetails(@Param("userId") Long userId);

    // Optimized: filter by date range at DB level, not in Java
    @Query("""
        SELECT e FROM Expense e
        JOIN FETCH e.user
        JOIN FETCH e.category
        WHERE e.user.id = :userId
        AND e.expenseDate BETWEEN :startDate AND :endDate
        """)
    List<Expense> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Optimized: filter by category at DB level
    @Query("""
        SELECT e FROM Expense e
        JOIN FETCH e.user
        JOIN FETCH e.category
        WHERE e.user.id = :userId
        AND e.category.id = :categoryId
""")
    List<Expense> findByUserIdAndCategoryIdWithDetails(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    // Optimized: filter by amount range at DB level
    @Query("""
        SELECT e FROM Expense e
        JOIN FETCH e.user
        JOIN FETCH e.category
        WHERE e.user.id = :userId
        AND e.amount BETWEEN :minAmount AND :maxAmount
        """)
    List<Expense> findByUserIdAndAmountRange(@Param("userId") Long userId, @Param("minAmount") Long minAmount, @Param("maxAmount") Long maxAmount);

    // Aggregate: total spent per category for a user
    @Query("""
SELECT e.category.name, SUM(e.amount)
FROM Expense e 
WHERE e.user.id = :userId
GROUP BY e.category.name
ORDER BY SUM(e.amount) DESC
""")
    List<Object[]> getTotalSpentPerCategory(@Param("userId") Long userId);

    // Aggregate: total spent in a month
    @Query("""
SELECT COALESCE(SUM(e.amount),0)
FROM Expense e
WHERE e.user.id = :userId
AND MONTH(e.expenseDate) = :month
AND YEAR(e.expenseDate) = :year
""")
    BigDecimal getTotalSpentInMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);
}
