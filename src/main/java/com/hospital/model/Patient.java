package com.hospital.model;

public class Patient extends Person {
    private int triageLevel; // 1 (En Düşük) - 5 (En Kritik/Acil)
    private int waitingMinutes; // Bekleme süresi (dakika)
    private String requiredSpecialty; // İhtiyaç duyulan doktor branşı
    private boolean requiresSurgery; // Ameliyat gerekiyor mu?
    private double calculatedPriorityScore; // Algoritmanın hesaplayacağı öncelik puanı

    public Patient(String id, String tcNo, String fullName, int age,
                   int triageLevel, int waitingMinutes, String requiredSpecialty, boolean requiresSurgery) {
        super(id, tcNo, fullName, age);
        setTriageLevel(triageLevel);
        setWaitingMinutes(waitingMinutes);
        setRequiredSpecialty(requiredSpecialty);
        this.requiresSurgery = requiresSurgery;
        this.calculatedPriorityScore = 0.0;
    }

    public int getTriageLevel() {
        return triageLevel;
    }

    // Kapsülleme: 1-5 dışındaki triyaj kodlarını reddeder (Birim testinde test edilecek!)
    public void setTriageLevel(int triageLevel) {
        if (triageLevel < 1 || triageLevel > 5) {
            throw new IllegalArgumentException("Triyaj seviyesi 1 ile 5 arasinda olmalidir!");
        }
        this.triageLevel = triageLevel;
    }

    public int getWaitingMinutes() {
        return waitingMinutes;
    }

    public void setWaitingMinutes(int waitingMinutes) {
        if (waitingMinutes < 0) {
            throw new IllegalArgumentException("Bekleme suresi negatif olamaz!");
        }
        this.waitingMinutes = waitingMinutes;
    }

    public String getRequiredSpecialty() {
        return requiredSpecialty;
    }

    public void setRequiredSpecialty(String requiredSpecialty) {
        if (requiredSpecialty == null || requiredSpecialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Gerekli brans bos olamaz!");
        }
        this.requiredSpecialty = requiredSpecialty.trim();
    }

    public boolean isRequiresSurgery() {
        return requiresSurgery;
    }

    public void setRequiresSurgery(boolean requiresSurgery) {
        this.requiresSurgery = requiresSurgery;
    }

    public double getCalculatedPriorityScore() {
        return calculatedPriorityScore;
    }

    public void setCalculatedPriorityScore(double calculatedPriorityScore) {
        this.calculatedPriorityScore = calculatedPriorityScore;
    }

    @Override
    public String getRoleSummary() {
        return String.format("[HASTA] %s (Yaş: %d) | Triyaj: %d/5 | Bekleme: %d dk | Branş: %s | Ameliyat: %s | Skor: %.2f",
                getFullName(), getAge(), triageLevel, waitingMinutes,
                requiredSpecialty, (requiresSurgery ? "EVET" : "HAYIR"), calculatedPriorityScore);
    }

    @Override
    public String toFileFormat() {
        return String.join(";", getId(), getTcNo(), getFullName(), String.valueOf(getAge()),
                String.valueOf(triageLevel), String.valueOf(waitingMinutes),
                requiredSpecialty, String.valueOf(requiresSurgery));
    }
}