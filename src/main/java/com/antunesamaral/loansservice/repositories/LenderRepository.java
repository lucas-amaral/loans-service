package com.antunesamaral.loansservice.repositories;

import com.antunesamaral.loansservice.entities.Lender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface LenderRepository extends JpaRepository<Lender, Integer> {
    @Query("select sum(l.available) FROM lenders l")
    BigInteger findTotalAvailable();
}
