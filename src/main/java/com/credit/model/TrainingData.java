package com.credit.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingData extends CreditData {
    private Integer eligibilityClass; // 0=Low, 1=Medium, 2=High
} 
