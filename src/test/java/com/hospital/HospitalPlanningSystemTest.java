package com.hospital;

import com.hospital.algorithm.HospitalSchedulerAlgorithm;
import com.hospital.model.Doctor;
import com.hospital.model.OperatingRoom;
import com.hospital.model.Patient;
import com.hospital.model.SurgerySchedule;
import com.hospital.pattern.EmergencyTriageStrategy;
import com.hospital.pattern.EntityFactory;
import com.hospital.pattern.StandardPolyclinicStrategy;
import com.hospital.repository.FileRepositoryManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalPlanningSystemTest {

    // TEST 1: Kapsülleme (Encapsulation) Testi
    @Test
    @DisplayName("1. Gecersiz triyaj seviyesi girildiginde IllegalArgumentException firlatilmali")
    void testInvalidTriageLevelThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Patient("PAT-99", "12345678901", "Test Hasta", 30, 8, 10, "Kardiyoloji", false);
        });
    }

    // TEST 2: Önceliklendirme Algoritması (Max-Heap / PriorityQueue) Testi
    @Test
    @DisplayName("2. Acil ve ameliyatlik hasta, öncelik kuyrugunda ilk siraya yerlesmeli")
    void testHighPriorityPatientComesFirst() {
        HospitalSchedulerAlgorithm scheduler = new HospitalSchedulerAlgorithm(new EmergencyTriageStrategy());

        Patient lowPriority = new Patient("P1", "11111111111", "Hafif Hasta", 25, 1, 10, "Kardiyoloji", false);
        Patient highPriority = new Patient("P2", "22222222222", "Kritik Hasta", 70, 5, 30, "Kardiyoloji", true);

        List<Patient> result = scheduler.prioritizePatients(Arrays.asList(lowPriority, highPriority));

        assertEquals("P2", result.get(0).getId(), "En kritik hasta listenin en basinda olmalidir!");
        assertTrue(result.get(0).getCalculatedPriorityScore() > result.get(1).getCalculatedPriorityScore());
    }

    // TEST 3: Starvation (Açlık Önleme / Bekleme Süresi) Testi
    @Test
    @DisplayName("3. Triyaj kodu ayni olan hastalardan daha uzun sure bekleyen one gecmeli")
    void testWaitingTimeIncreasesPriority() {
        HospitalSchedulerAlgorithm scheduler = new HospitalSchedulerAlgorithm(new EmergencyTriageStrategy());

        Patient shortWait = new Patient("P1", "11111111111", "Yeni Gelen", 40, 3, 10, "Noroloji", false);
        Patient longWait = new Patient("P2", "22222222222", "Cok Bekleyen", 40, 3, 120, "Noroloji", false);

        List<Patient> sorted = scheduler.prioritizePatients(Arrays.asList(shortWait, longWait));

        assertEquals("P2", sorted.get(0).getId(), "Uzun sure bekleyen hasta öncelik kazanmalidir!");
    }

    // TEST 4: Strategy Tasarım Kalıbı (Çok Biçimlilik / Polymorphism) Testi
    @Test
    @DisplayName("4. Strateji degistirildiginde hastanin öncelik puani farkli hesaplanmali")
    void testStrategyPatternChangesScoreCalculation() {
        Patient patient = new Patient("P1", "11111111111", "Ornek Hasta", 70, 4, 50, "Kardiyoloji", true);

        EmergencyTriageStrategy emergencyStrategy = new EmergencyTriageStrategy();
        StandardPolyclinicStrategy polyclinicStrategy = new StandardPolyclinicStrategy();

        double emergencyScore = emergencyStrategy.calculateScore(patient);
        double polyclinicScore = polyclinicStrategy.calculateScore(patient);

        assertNotEquals(emergencyScore, polyclinicScore, "Farkli stratejiler farkli puanlar uretmelidir!");
    }

    // TEST 5: Greedy Ameliyathane ve Doktor Çakışma Önleme Testi
    @Test
    @DisplayName("5. Ayni branstaki iki ameliyat hastasi ayni saat dilimine cakisacak sekilde atanmamali")
    void testGreedySchedulingPreventsTimeConflict() {
        HospitalSchedulerAlgorithm scheduler = new HospitalSchedulerAlgorithm(new EmergencyTriageStrategy());

        Doctor doc = new Doctor("D1", "11111111111", "Ahmet Yilmaz", 45, "Genel Cerrahi");
        OperatingRoom room = new OperatingRoom("OR1", "Ameliyathane 1", "Genel Cerrahi");

        Patient p1 = new Patient("P1", "22222222222", "Hasta 1", 50, 5, 20, "Genel Cerrahi", true);
        Patient p2 = new Patient("P2", "33333333333", "Hasta 2", 40, 4, 20, "Genel Cerrahi", true);

        List<SurgerySchedule> schedules = scheduler.schedulePatients(
                Arrays.asList(p1, p2),
                Collections.singletonList(doc),
                Collections.singletonList(room)
        );

        assertEquals(2, schedules.size());
        assertEquals(8, schedules.get(0).getScheduledHour(), "Ilk hasta saat 08:00'a atanmali");
        assertEquals(9, schedules.get(1).getScheduledHour(), "Ikinci hasta cakismamasi icin saat 09:00'a atanmali");
    }

    // TEST 6: Branş Uyuşmazlığı Kontrol Testi
    @Test
    @DisplayName("6. Hastanin ihtiyac duydugu bransta doktor yoksa planlama yapilamamali")
    void testUnmatchedSpecialtyIsNotScheduled() {
        HospitalSchedulerAlgorithm scheduler = new HospitalSchedulerAlgorithm(new EmergencyTriageStrategy());

        Doctor cardioDoc = new Doctor("D1", "11111111111", "Kardiyolog", 45, "Kardiyoloji");
        OperatingRoom generalRoom = new OperatingRoom("OR1", "Genel Oda", "GENEL");
        Patient neuroPatient = new Patient("P1", "22222222222", "Noroloji Hastasi", 35, 4, 15, "Noroloji", false);

        List<SurgerySchedule> schedules = scheduler.schedulePatients(
                Collections.singletonList(neuroPatient),
                Collections.singletonList(cardioDoc),
                Collections.singletonList(generalRoom)
        );

        assertTrue(schedules.isEmpty(), "Uygun bransta doktor olmadiginda randevu olusturulmamalidir!");
    }

    // TEST 7: Factory Pattern ve Kalıcı Veri Formatı Dönüşüm Testi
    @Test
    @DisplayName("7. Dosya formatina cevrildikten sonra Factory ile okunan nesne verilerini korumali")
    void testEntityFactorySerializationAndDeserialization() {
        Patient original = new Patient("PAT-10", "99999999999", "Mehmet Oz", 60, 4, 45, "Kardiyoloji", true);
        String line = original.toFileFormat();

        Patient reconstructed = EntityFactory.createPatientFromLine(line);

        assertEquals(original.getId(), reconstructed.getId());
        assertEquals(original.getTcNo(), reconstructed.getTcNo());
        assertEquals(original.getFullName(), reconstructed.getFullName());
        assertEquals(original.getTriageLevel(), reconstructed.getTriageLevel());
        assertEquals(original.isRequiresSurgery(), reconstructed.isRequiresSurgery());
    }

    // TEST 8: Singleton Tasarım Kalıbı ve Dosya Okuma/Yazma Testi
    @Test
    @DisplayName("8. Singleton FileRepositoryManager tek ornek donmeli ve dosyaya veri yazip okuyabilmeli")
    void testSingletonRepositorySaveAndLoad() throws Exception {
        FileRepositoryManager<Doctor> repo1 = FileRepositoryManager.getInstance();
        FileRepositoryManager<Doctor> repo2 = FileRepositoryManager.getInstance();

        assertSame(repo1, repo2, "Singleton sinifi her zaman ayni referansi dondurmelidir!");

        String tempFile = "data/test_doctors_temp.txt";
        Doctor doc = new Doctor("DOC-TEST", "12312312312", "Test Doktor", 40, "Ortopedi");
        doc.bookHour(10);

        repo1.saveAll(tempFile, Collections.singletonList(doc));
        List<Doctor> loaded = repo2.loadAll(tempFile, EntityFactory::createDoctorFromLine);

        assertEquals(1, loaded.size());
        assertEquals("DOC-TEST", loaded.get(0).getId());
        assertFalse(loaded.get(0).isAvailableAt(10), "Kaydedilen dolu saat geri yuklendiginde de dolu olmali!");

        // Test sonrası geçici dosyayı temizle
        Files.deleteIfExists(Path.of(tempFile));
    }
}