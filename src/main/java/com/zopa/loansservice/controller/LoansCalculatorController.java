package com.zopa.loansservice.controller;

import java.math.BigInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zopa.loansservice.services.LoansCalculatorService;

@RestController
@RequestMapping("/loans")
public class LoansCalculatorController {

    private final LoansCalculatorService loansCalculatorService;

    public LoansCalculatorController(LoansCalculatorService loansCalculatorService) {
        this.loansCalculatorService = loansCalculatorService;
    }

    @GetMapping
    public ResponseEntity<?> getLowestLoansByAmount(@RequestParam("amount") BigInteger amount) {
        return loansCalculatorService.getLowestLoansByAmount(amount)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
