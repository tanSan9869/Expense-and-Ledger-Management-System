package com.ELSystem.elsystem.repository;

import com.ELSystem.elsystem.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionLogRepository extends JpaRepository<TransactionLog,Long> {
    List<TransactionLog> findByExpenseUserIdOrderByTimestampDesc(Long userId);
}
