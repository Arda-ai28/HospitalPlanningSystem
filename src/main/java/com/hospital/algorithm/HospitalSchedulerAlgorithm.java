package com.hospital.algorithm;

import com.hospital.model.Doctor;
import com.hospital.model.OperatingRoom;
import com.hospital.model.Patient;
import com.hospital.model.SurgerySchedule;
import com.hospital.pattern.PriorityStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class HospitalSchedulerAlgorithm {

    private PriorityStrategy strategy;

    public HospitalSchedulerAlgorithm(PriorityStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PriorityStrategy strategy) {
        this.strategy = strategy;
    }

    public PriorityStrategy getStrategy() {
        return strategy;
    }

    // 1. AŞAMA ALGORİTMASI: Max-Heap (PriorityQueue) ile Hasta Önceliklendirme
    public List<Patient> prioritizePatients(List<Patient> patients) {
        for (Patient p : patients) {
            double score = strategy.calculateScore(p);
            p.setCalculatedPriorityScore(score);
        }

        // Yüksek puanlı hastayı en üste koyan Max-Heap karşılaştırıcısı
        PriorityQueue<Patient> maxHeap = new PriorityQueue<>(
                (p1, p2) -> Double.compare(p2.getCalculatedPriorityScore(), p1.getCalculatedPriorityScore())
        );

        maxHeap.addAll(patients);

        List<Patient> sortedList = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            sortedList.add(maxHeap.poll());
        }
        return sortedList;
    }

    // 2. AŞAMA ALGORİTMASI: Greedy (Açgözlü) Doktor ve Ameliyathane Zaman Çizelgeleme
    public List<SurgerySchedule> schedulePatients(List<Patient> patients,
                                                  List<Doctor> doctors,
                                                  List<OperatingRoom> rooms) {
        List<Patient> prioritizedQueue = prioritizePatients(patients);
        List<SurgerySchedule> schedules = new ArrayList<>();
        int scheduleCounter = 1;

        for (Patient patient : prioritizedQueue) {
            boolean scheduled = false;

            // Mesai saatleri (08:00 - 17:00) arasında en erken uygun zaman dilimini ara (Greedy)
            for (int hour = 8; hour <= 17 && !scheduled; hour++) {
                Doctor availableDoctor = findAvailableDoctor(doctors, patient.getRequiredSpecialty(), hour);

                if (availableDoctor != null) {
                    if (patient.isRequiresSurgery()) {
                        // Ameliyat gerekiyorsa aynı saatte uygun ameliyathane de boş olmalı
                        OperatingRoom availableRoom = findAvailableRoom(rooms, patient.getRequiredSpecialty(), hour);
                        if (availableRoom != null) {
                            availableDoctor.bookHour(hour);
                            availableRoom.reserveHour(hour);

                            String planId = "PLN-" + (100 + scheduleCounter++);
                            schedules.add(new SurgerySchedule(planId, patient, availableDoctor, availableRoom, hour));
                            scheduled = true;
                        }
                    } else {
                        // Ameliyat gerekmiyorsa sadece poliklinik doktorunun boş olması yeterli
                        availableDoctor.bookHour(hour);
                        String planId = "PLN-" + (100 + scheduleCounter++);
                        schedules.add(new SurgerySchedule(planId, patient, availableDoctor, null, hour));
                        scheduled = true;
                    }
                }
            }
        }
        return schedules;
    }

    private Doctor findAvailableDoctor(List<Doctor> doctors, String specialty, int hour) {
        for (Doctor doc : doctors) {
            if (doc.getSpecialty().equalsIgnoreCase(specialty) && doc.isAvailableAt(hour)) {
                return doc;
            }
        }
        return null;
    }

    private OperatingRoom findAvailableRoom(List<OperatingRoom> rooms, String specialty, int hour) {
        for (OperatingRoom room : rooms) {
            boolean specialtyMatches = room.getSupportedSpecialty().equalsIgnoreCase(specialty)
                    || room.getSupportedSpecialty().equalsIgnoreCase("GENEL");
            if (specialtyMatches && room.isAvailableAt(hour)) {
                return room;
            }
        }
        return null;
    }
}