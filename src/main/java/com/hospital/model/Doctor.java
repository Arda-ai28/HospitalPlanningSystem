package com.hospital.model;

import java.util.HashSet;
import java.util.Set;

public class Doctor extends Person {
    private String specialty;
    private Set<Integer> busyHours;

    public Doctor(String id, String tcNo, String fullName, int age, String specialty) {
        super(id, tcNo, fullName, age);
        setSpecialty(specialty);
        this.busyHours = new HashSet<>();
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Uzmanlik alani bos olamaz!");
        }
        this.specialty = specialty.trim();
    }

    public Set<Integer> getBusyHours() {
        return new HashSet<>(busyHours);
    }

    public boolean isAvailableAt(int hour) {
        return !busyHours.contains(hour);
    }

    public void bookHour(int hour) {
        if (hour < 8 || hour > 17) {
            throw new IllegalArgumentException("Calisma saatleri 08:00 - 17:00 arasindadir!");
        }
        if (!isAvailableAt(hour)) {
            throw new IllegalStateException("Doktor saat " + hour + ":00'da zaten dolu!");
        }
        busyHours.add(hour);
    }

    @Override
    public String getRoleSummary() {
        return String.format("[DOKTOR] Dr. %s | Branş: %s | Dolu Saatler: %s",
                getFullName(), specialty, busyHours.toString());
    }

    @Override
    public String toFileFormat() {
        String hours = busyHours.isEmpty() ? "NONE" :
                String.join(",", busyHours.stream().map(String::valueOf).toList());
        return String.join(";", getId(), getTcNo(), getFullName(),
                String.valueOf(getAge()), specialty, hours);
    }
}