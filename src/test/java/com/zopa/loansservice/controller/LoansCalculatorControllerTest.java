package com.zopa.loansservice.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.zopa.loansservice.entities.Loans;
import com.zopa.loansservice.services.LoansCalculatorService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoansCalculatorController.class)
class LoansCalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoansCalculatorService loansCalculatorService;

    @Test
    void getLowestLoansByAmount() throws Exception {
        final BigInteger amount = new BigInteger("1000");
        final Loans loans = new Loans("£1000","7.0%", "£30.78", "£1108.10");
        final String responseBody = "{" +
                "    \"requestedAmount\": \"£1000\"," +
                "    \"annualInterestRate\": \"7.0%\"," +
                "    \"monthlyRepayment\": \"£30.78\"," +
                "    \"totalRepayment\": \"£1108.10\"" +
                "}";

        when(loansCalculatorService.getLowestLoansByAmount(amount)).thenReturn(Optional.of(loans));

        mockMvc.perform(get("/loans?amount={amount}", amount))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));

        verify(loansCalculatorService).getLowestLoansByAmount(amount);
    }

    @Test
    void getLowestLoansByAmountShouldReturn404IfThereIsNoData() throws Exception {
        final BigInteger amount = new BigInteger("1000");

        when(loansCalculatorService.getLowestLoansByAmount(amount)).thenReturn(Optional.empty());

        mockMvc.perform(get("/loans?amount={amount}", amount))
                .andExpect(status().isNotFound());

        verify(loansCalculatorService).getLowestLoansByAmount(amount);
    }
}