package com.antunesamaral.loansservice.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "lenders")
public class Lender {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, scale = 3, precision = 19)
    private BigDecimal rate;
    @Column(nullable = false)
    private BigInteger available;

    public Lender() {}

    private Lender(Integer id, String name, BigDecimal rate, BigInteger available) {
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.available = available;
    }

    public static Lender of(String name, BigDecimal rate, BigInteger available) {
        return new Lender(null, name, rate, available);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigInteger getAvailable() {
        return available;
    }
}
