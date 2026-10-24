package com.hospital.pattern;

import com.hospital.model.Doctor;
import com.hospital.model.OperatingRoom;
import com.hospital.model.Patient;

public class EntityFactory {

    public static Doctor createDoctorFromLine(String line) {
        String[] parts = line.split(";");
        Doctor doctor = new Doctor(
                parts[0], // id
                parts[1], // tcNo
                parts[2], // fullName
                Integer.parseInt(parts[3]), // age
                parts[4]  // specialty
        );
        if (parts.length > 5 && !parts[5].equals("NONE") && !parts[5].isEmpty()) {
            for (String h : parts[5].split(",")) {
                doctor.bookHour(Integer.parseInt(h.trim()));
            }
        }
        return doctor;
    }

    public static Patient createPatientFromLine(String line) {
        String[] parts = line.split(";");
        return new Patient(
                parts[0], // id
                parts[1], // tcNo
                parts[2], // fullName
                Integer.parseInt(parts[3]), // age
                Integer.parseInt(parts[4]), // triageLevel
                Integer.parseInt(parts[5]), // waitingMinutes
                parts[6], // requiredSpecialty
                Boolean.parseBoolean(parts[7]) // requiresSurgery
        );
    }

    public static OperatingRoom createRoomFromLine(String line) {
        String[] parts = line.split(";");
        OperatingRoom room = new OperatingRoom(parts[0], parts[1], parts[2]);
        if (parts.length > 3 && !parts[3].equals("NONE") && !parts[3].isEmpty()) {
            for (String h : parts[3].split(",")) {
                room.reserveHour(Integer.parseInt(h.trim()));
            }
        }
        return room;
    }
}