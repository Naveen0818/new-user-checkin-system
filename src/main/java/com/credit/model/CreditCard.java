package com.credit.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type;
    private Double annualFee;
    private Double apr;
    private String rewards;
    private Integer creditScoreRequired;
    private List<String> features;
    private String gptExplanation;
} 