package com.hospital.pattern;

import com.hospital.model.Patient;

public interface PriorityStrategy {
    double calculateScore(Patient patient);
    String getStrategyName();
}