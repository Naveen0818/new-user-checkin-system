package com.credit.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreditData {
    @NotNull
    @Min(0)
    private Double ageOfCredit;
    
    @NotNull
    @Min(0)
    private Integer derogatoryMarks;
    
    @NotNull
    @Min(0)
    private Double creditUtilization;
    
    @NotNull
    @Min(0)
    private Integer missedPayments;
    
    @NotNull
    @Min(0)
    private Integer creditInquiries;
    
    @NotNull
    @Min(0)
    private Integer totalAccounts;
    
    @NotNull
    @Min(0)
    private Double creditLimit;
    
    @NotNull
    @Min(0)
    private Double income;
} 