package com.hospital.model;

import java.util.HashSet;
import java.util.Set;

public class OperatingRoom extends BaseEntity {
    private String roomName;
    private String supportedSpecialty; // Hangi branş ameliyatlarına uygun (veya "GENEL")
    private Set<Integer> reservedHours; // Dolu olduğu saatler (8 - 17 arası)

    public OperatingRoom(String id, String roomName, String supportedSpecialty) {
        super(id);
        setRoomName(roomName);
        setSupportedSpecialty(supportedSpecialty);
        this.reservedHours = new HashSet<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        if (roomName == null || roomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ameliyathane adi bos olamaz!");
        }
        this.roomName = roomName.trim();
    }

    public String getSupportedSpecialty() {
        return supportedSpecialty;
    }

    public void setSupportedSpecialty(String supportedSpecialty) {
        if (supportedSpecialty == null || supportedSpecialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Desteklenen brans bos olamaz!");
        }
        this.supportedSpecialty = supportedSpecialty.trim();
    }

    public Set<Integer> getReservedHours() {
        return new HashSet<>(reservedHours);
    }

    public boolean isAvailableAt(int hour) {
        return !reservedHours.contains(hour);
    }

    public void reserveHour(int hour) {
        if (hour < 8 || hour > 17) {
            throw new IllegalArgumentException("Ameliyathane sadece 08:00 - 17:00 arasi rezerve edilebilir!");
        }
        if (!isAvailableAt(hour)) {
            throw new IllegalStateException(roomName + " saat " + hour + ":00'da zaten dolu!");
        }
        reservedHours.add(hour);
    }

    @Override
    public String toFileFormat() {
        String hours = reservedHours.isEmpty() ? "NONE" :
                String.join(",", reservedHours.stream().map(String::valueOf).toList());
        return String.join(";", getId(), roomName, supportedSpecialty, hours);
    }

    @Override
    public String toString() {
        return String.format("[AMELİYATHANE] %s (%s) | Branş: %s | Dolu Saatler: %s",
                getId(), roomName, supportedSpecialty, reservedHours.toString());
    }
}