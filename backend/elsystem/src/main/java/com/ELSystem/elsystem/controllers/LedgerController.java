package com.ELSystem.elsystem.controllers;

import com.ELSystem.elsystem.dto.response.LedgerResponse;
import com.ELSystem.elsystem.model.LedgerEntry;
import com.ELSystem.elsystem.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class LedgerController {
    private final LedgerService ledgerService;

    @GetMapping("/balance/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId){
            return new ResponseEntity<>(ledgerService.getBalance(userId), HttpStatus.OK);
    }

    @GetMapping("/entries/{userId}")
    public ResponseEntity<List<LedgerResponse>> getEntries(@PathVariable Long userId){
        return new ResponseEntity<>(ledgerService.getLedgerEntries(userId),HttpStatus.OK);
    }
}
