package com.antunesamaral.loansservice;

import com.antunesamaral.loansservice.services.LoansCalculatorService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigInteger;

@SpringBootApplication
public class LoansServiceApplication implements ApplicationRunner {

	private final LoansCalculatorService loansCalculatorService;

	public LoansServiceApplication(LoansCalculatorService loansCalculatorService) {
		this.loansCalculatorService = loansCalculatorService;
	}

	public static void main(String[] args) {
		SpringApplication.run(LoansServiceApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		args.getNonOptionArgs().stream().findAny().ifPresent(arg -> {
			final BigInteger amount = new BigInteger(arg);
			loansCalculatorService.lowestInterestLoans(amount);
		});
	}
}
