package com.credit.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingData extends CreditData {
    private boolean eligible; // Target variable for training
} 