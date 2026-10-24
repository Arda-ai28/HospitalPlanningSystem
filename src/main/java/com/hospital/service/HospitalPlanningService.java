package com.hospital.service;

import com.hospital.algorithm.HospitalSchedulerAlgorithm;
import com.hospital.model.Doctor;
import com.hospital.model.OperatingRoom;
import com.hospital.model.Patient;
import com.hospital.model.SurgerySchedule;
import com.hospital.pattern.EntityFactory;
import com.hospital.pattern.PriorityStrategy;
import com.hospital.repository.IRepository;

import java.util.ArrayList;
import java.util.List;

public class HospitalPlanningService {

    private static final String DOCTORS_FILE = "data/doctors.txt";
    private static final String PATIENTS_FILE = "data/patients.txt";
    private static final String ROOMS_FILE = "data/rooms.txt";
    private static final String SCHEDULES_FILE = "data/schedules.txt";

    // SOLID - Dependency Inversion: Somut sınıfa değil, IRepository arayüzüne bağımlıyız
    private final IRepository<Doctor> doctorRepo;
    private final IRepository<Patient> patientRepo;
    private final IRepository<OperatingRoom> roomRepo;
    private final IRepository<SurgerySchedule> scheduleRepo;

    private final HospitalSchedulerAlgorithm schedulerAlgorithm;

    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<OperatingRoom> rooms;
    private List<SurgerySchedule> schedules;

    public HospitalPlanningService(IRepository<Doctor> doctorRepo,
                                   IRepository<Patient> patientRepo,
                                   IRepository<OperatingRoom> roomRepo,
                                   IRepository<SurgerySchedule> scheduleRepo,
                                   PriorityStrategy initialStrategy) {
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.roomRepo = roomRepo;
        this.scheduleRepo = scheduleRepo;
        this.schedulerAlgorithm = new HospitalSchedulerAlgorithm(initialStrategy);

        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.schedules = new ArrayList<>();
    }

    // Kalıcı verileri dosyalardan yükler, dosya yoksa başlangıç verisi oluşturur
    public void initializeSystemData() {
        this.doctors = doctorRepo.loadAll(DOCTORS_FILE, EntityFactory::createDoctorFromLine);
        this.patients = patientRepo.loadAll(PATIENTS_FILE, EntityFactory::createPatientFromLine);
        this.rooms = roomRepo.loadAll(ROOMS_FILE, EntityFactory::createRoomFromLine);

        if (doctors.isEmpty() && rooms.isEmpty() && patients.isEmpty()) {
            seedDefaultData();
            saveAllData();
        }
    }

    private void seedDefaultData() {
        // Örnek Doktorlar
        doctors.add(new Doctor("DOC-1", "11111111111", "Ahmet Yilmaz", 45, "Kardiyoloji"));
        doctors.add(new Doctor("DOC-2", "22222222222", "Zeynep Kaya", 38, "Genel Cerrahi"));
        doctors.add(new Doctor("DOC-3", "33333333333", "Mehmet Demir", 50, "Noroloji"));

        // Örnek Ameliyathaneler
        rooms.add(new OperatingRoom("OR-1", "Ameliyathane A (KVC)", "Kardiyoloji"));
        rooms.add(new OperatingRoom("OR-2", "Ameliyathane B (Genel)", "GENEL"));

        // Örnek Hastalar
        patients.add(new Patient("PAT-1", "44444444444", "Ali Veli", 70, 5, 40, "Kardiyoloji", true));
        patients.add(new Patient("PAT-2", "55555555555", "Ayse Fatma", 28, 2, 90, "Kardiyoloji", false));
        patients.add(new Patient("PAT-3", "66666666666", "Canan Can", 55, 4, 30, "Genel Cerrahi", true));
        patients.add(new Patient("PAT-4", "77777777777", "Burak Оz", 34, 3, 120, "Noroloji", false));
    }

    public void saveAllData() {
        doctorRepo.saveAll(DOCTORS_FILE, doctors);
        patientRepo.saveAll(PATIENTS_FILE, patients);
        roomRepo.saveAll(ROOMS_FILE, rooms);
        scheduleRepo.saveAll(SCHEDULES_FILE, schedules);
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        patientRepo.saveAll(PATIENTS_FILE, patients);
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        doctorRepo.saveAll(DOCTORS_FILE, doctors);
    }

    public void addOperatingRoom(OperatingRoom room) {
        rooms.add(room);
        roomRepo.saveAll(ROOMS_FILE, rooms);
    }

    public void changePriorityStrategy(PriorityStrategy newStrategy) {
        schedulerAlgorithm.setStrategy(newStrategy);
    }

    public String getActiveStrategyName() {
        return schedulerAlgorithm.getStrategy().getStrategyName();
    }

    public List<Patient> getPrioritizedPatients() {
        return schedulerAlgorithm.prioritizePatients(patients);
    }

    public List<SurgerySchedule> runSchedulingAlgorithm() {
        // Yeni planlama öncesi mevcut doktor/oda saatlerini sıfırlayıp baştan optimal dağıtım yapalım
        resetSchedulesAndHours();
        this.schedules = schedulerAlgorithm.schedulePatients(patients, doctors, rooms);
        saveAllData();
        return schedules;
    }

    private void resetSchedulesAndHours() {
        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            doctors.set(i, new Doctor(d.getId(), d.getTcNo(), d.getFullName(), d.getAge(), d.getSpecialty()));
        }
        for (int i = 0; i < rooms.size(); i++) {
            OperatingRoom r = rooms.get(i);
            rooms.set(i, new OperatingRoom(r.getId(), r.getRoomName(), r.getSupportedSpecialty()));
        }
        schedules.clear();
    }

    public List<Doctor> getDoctors() { return doctors; }
    public List<Patient> getPatients() { return patients; }
    public List<OperatingRoom> getRooms() { return rooms; }
    public List<SurgerySchedule> getSchedules() { return schedules; }
}