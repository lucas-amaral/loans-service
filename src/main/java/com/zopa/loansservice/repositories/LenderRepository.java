package com.zopa.loansservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zopa.loansservice.entities.Lender;

import java.math.BigInteger;

public interface LenderRepository extends JpaRepository<Lender, Integer> {
    @Query("select sum(l.available) FROM lenders l")
    BigInteger findTotalAvailable();
}
