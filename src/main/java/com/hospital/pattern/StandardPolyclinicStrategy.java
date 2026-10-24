package com.hospital.pattern;

import com.hospital.model.Patient;

public class StandardPolyclinicStrategy implements PriorityStrategy {

    @Override
    public double calculateScore(Patient patient) {
        // Poliklinikte bekleme süresi (FIFO mantığına yakın) daha baskındır
        double score = patient.getWaitingMinutes() * 2.5;
        score += patient.getTriageLevel() * 10.0;

        if (patient.getAge() >= 65) {
            score += 35.0; // Poliklinikte 65 yaş üstü önceliği
        }
        return score;
    }

    @Override
    public String getStrategyName() {
        return "Standart Poliklinik (Bekleme ve Yaş Odaklı) Stratejisi";
    }
}