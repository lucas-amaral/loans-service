# Loans Service

It is an application to find a quote from market of lenders for 36-month
loans that applies interest on a monthly basis.

This application chooses a combination of lenders’ offers which
gives the lowest possible rate. 
The monthly repayment and the total repayment amounts
should be shown in addition to the amount requested and the annual interest rate for the
quote.

## Pre requirements

The application just accept one argument:
[loan_amount]

A quote may be requested in any £100 increment between £1000 and £15000 inclusive.

## How to run

As it is a spring-boot based application, to run we can do:

```mvn spring-boot:run -Dspring-boot.run.arguments=1000```

The argument informed might be the requested amount.

### Output format

- Requested amount: £XXXX
- Annual Interest Rate: X.X%
- Monthly repayment: £XXXX.XX
- Total repayment: £XXXX.XX

## Lenders Data

Currently, loans service store the data on `H2` in-memory database. 

The list of lenders is upload by `data.sql` script. We can find and change it in `/src/resources/data.sql`.

## Monthly repayment calculation

The monthly repayments should spread the total repayment cost over the term of the loan.

For more information about how to calculate it, please check the link below:

https://en.wikipedia.org/wiki/Amortization_calculator#The_formula

## Rest API

Loans service also provides a rest APIs for access 

| Http method        | Path           | Description | Response Body  |
| -------------      |:-------------: | -----       |-----           |
| GET      | /loans?amount={requestedAmount} | Fetch the lowest loan by a required amount | `{ "requestedAmount": "£1700", "annualInterestRate": "7.2%", "monthlyRepayment": "£52.46", "totalRepayment": "£1888.55"  }` |

E.g. `curl 'localhost:8080/loans?amount=1700'`
