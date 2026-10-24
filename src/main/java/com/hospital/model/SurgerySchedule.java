package com.hospital.model;

public class SurgerySchedule extends BaseEntity {
    private Patient patient;
    private Doctor doctor;
    private OperatingRoom operatingRoom; // Sadece poliklinik muayenesiyse null olabilir
    private int scheduledHour; // Örn: 9 (09:00 - 10:00)

    public SurgerySchedule(String id, Patient patient, Doctor doctor, OperatingRoom operatingRoom, int scheduledHour) {
        super(id);
        if (patient == null || doctor == null) {
            throw new IllegalArgumentException("Hasta ve Doktor bos olamaz!");
        }
        if (scheduledHour < 8 || scheduledHour > 17) {
            throw new IllegalArgumentException("Planlama saati 08:00 - 17:00 arasinda olmalidir!");
        }
        this.patient = patient;
        this.doctor = doctor;
        this.operatingRoom = operatingRoom;
        this.scheduledHour = scheduledHour;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public OperatingRoom getOperatingRoom() {
        return operatingRoom;
    }

    public int getScheduledHour() {
        return scheduledHour;
    }

    @Override
    public String toFileFormat() {
        String roomId = (operatingRoom != null) ? operatingRoom.getId() : "POLYCLINIC";
        return String.join(";", getId(), patient.getId(), doctor.getId(), roomId, String.valueOf(scheduledHour));
    }

    @Override
    public String toString() {
        String roomInfo = (operatingRoom != null) ? operatingRoom.getRoomName() : "Poliklinik Muayenesi";
        return String.format("[PLAN #%s] Saat: %02d:00 | Hasta: %s (Skor: %.1f) | Doktor: Dr. %s (%s) | Yer: %s",
                getId(), scheduledHour, patient.getFullName(), patient.getCalculatedPriorityScore(),
                doctor.getFullName(), doctor.getSpecialty(), roomInfo);
    }
}