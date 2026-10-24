package com.hospital.pattern;

import com.hospital.model.Patient;

public class EmergencyTriageStrategy implements PriorityStrategy {

    @Override
    public double calculateScore(Patient patient) {
        // 1. Triyaj Katsayısı (Her seviye 25 puan)
        double score = patient.getTriageLevel() * 25.0;

        // 2. Bekleme Süresi Etkisi (Her dakika 1.5 puan ekler - Uzun bekleyen üst sıraya çıkar)
        score += patient.getWaitingMinutes() * 1.5;

        // 3. Yaş Risk Faktörü (65 yaş üstü veya 7 yaş altı için +20 kritik puan)
        if (patient.getAge() >= 65 || patient.getAge() <= 7) {
            score += 20.0;
        }

        // 4. Cerrahi Müdahale Gereksinimi (+30 puan)
        if (patient.isRequiresSurgery()) {
            score += 30.0;
        }

        return score;
    }

    @Override
    public String getStrategyName() {
        return "Acil Triyaj ve Cerrahi Öncelik Stratejisi";
    }
}