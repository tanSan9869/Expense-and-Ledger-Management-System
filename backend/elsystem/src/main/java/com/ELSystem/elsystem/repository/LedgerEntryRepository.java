package com.ELSystem.elsystem.repository;

import com.ELSystem.elsystem.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,Long> {
    List<LedgerEntry> findByUserIdOrderByEntryDateDesc(Long userId);

    // Gets the latest entry to read the current running balance
    Optional<LedgerEntry> findTopByUserIdOrderByEntryDateDesc(Long userId);

    Optional<LedgerEntry> findByExpenseId(Long expenseId);

    @Query("""
    SELECT l FROM LedgerEntry l
    WHERE l.user.id = :userId
    AND l.entryDate > :afterDate
    ORDER BY l.entryDate ASC
    """)
    List<LedgerEntry> findByUserIdAfterDate(
            @Param("userId") Long userId,
            @Param("afterDate") java.time.LocalDateTime afterDate
    );

    // Computes total balance directly in the DB — no Java loops
    @Query("SELECT COALESCE(SUM(l.creditAmount) - SUM(l.debitAmount),0) FROM LedgerEntry l WHERE l.user.id = :userId")
    java.math.BigDecimal calculateBalanceByUserId(@Param("userId") Long userId);
}
