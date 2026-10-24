package com.hospital.ui;

import com.hospital.model.Doctor;
import com.hospital.model.OperatingRoom;
import com.hospital.model.Patient;
import com.hospital.model.Person;
import com.hospital.model.SurgerySchedule;
import com.hospital.pattern.EmergencyTriageStrategy;
import com.hospital.pattern.StandardPolyclinicStrategy;
import com.hospital.repository.FileRepositoryManager;
import com.hospital.service.HospitalPlanningService;

import java.util.List;
import java.util.Scanner;

public class ConsoleMenuApp {

    private final HospitalPlanningService service;
    private final Scanner scanner;

    public ConsoleMenuApp() {
        // Singleton Repository örnekleri ve varsayılan Strategy ile servisi başlatıyoruz
        this.service = new HospitalPlanningService(
                FileRepositoryManager.getInstance(),
                FileRepositoryManager.getInstance(),
                FileRepositoryManager.getInstance(),
                FileRepositoryManager.getInstance(),
                new EmergencyTriageStrategy()
        );
        this.service.initializeSystemData();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        System.out.println("==========================================================");
        System.out.println("   HASTANE PLANLAMA VE CIZELGELEME SISTEMINE HOS GELDINIZ ");
        System.out.println("==========================================================");

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> listAllEntitiesPolymorphic();
                    case "2" -> showPrioritizedPatients();
                    case "3" -> runSchedulingAndPrint();
                    case "4" -> addNewPatientMenu();
                    case "5" -> addNewDoctorMenu();
                    case "6" -> addNewRoomMenu();
                    case "7" -> switchStrategyMenu();
                    case "0" -> {
                        service.saveAllData();
                        System.out.println("Veriler 'data/' klasorune kalici olarak kaydedildi. Cikis yapiliyor...");
                        running = false;
                    }
                    default -> System.out.println("[UYARI] Gecersiz secim! Lutfen 0-7 arasinda bir deger girin.");
                }
            } catch (Exception e) {
                System.out.println("[HATA] Islem sirasinda kural ihlali: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----------------------------------------------------------");
        System.out.println("Aktif Onceliklendirme Stratejisi: " + service.getActiveStrategyName());
        System.out.println("----------------------------------------------------------");
        System.out.println("1. Tum Doktor, Hasta ve Ameliyathaneleri Listele (Polymorphism)");
        System.out.println("2. Hastalari Oncelik Puanina Gore Sirala (Max-Heap Algoritmasi)");
        System.out.println("3. Doktor ve Ameliyathane Cizelgeleme Algoritmasini Calistir (Greedy)");
        System.out.println("4. Yeni Hasta Kaydi Ekle");
        System.out.println("5. Yeni Doktor Kaydi Ekle");
        System.out.println("6. Yeni Ameliyathane Ekle");
        System.out.println("7. Onceliklendirme Stratejisini Degistir (Strategy Pattern)");
        System.out.println("0. Kaydet ve Cikis Yap");
        System.out.print("Seciminiz: ");
    }

    // Liskov Substitution & Polymorphism gösterimi: Person referansı üzerinden alt sınıfları çağırma
    private void listAllEntitiesPolymorphic() {
        System.out.println("\n--- KAYITLI DOKTORLAR VE HASTALAR (Cok Bicimlilik / Polymorphism) ---");
        List<Person> people = new java.util.ArrayList<>();
        people.addAll(service.getDoctors());
        people.addAll(service.getPatients());

        for (Person person : people) {
            System.out.println(person.getRoleSummary());
        }

        System.out.println("\n--- KAYITLI AMELIYATHANELER ---");
        for (OperatingRoom room : service.getRooms()) {
            System.out.println(room.toString());
        }
    }

    private void showPrioritizedPatients() {
        System.out.println("\n--- ONCELIK SIRASINA GORE HASTA KUYRUGU (" + service.getActiveStrategyName() + ") ---");
        List<Patient> sorted = service.getPrioritizedPatients();
        int rank = 1;
        for (Patient p : sorted) {
            System.out.printf("%d. %s%n", rank++, p.getRoleSummary());
        }
    }

    private void runSchedulingAndPrint() {
        System.out.println("\n--- DOKTOR VE AMELIYATHANE CIZELGELEME SONUCLARI ---");
        List<SurgerySchedule> schedules = service.runSchedulingAlgorithm();
        if (schedules.isEmpty()) {
            System.out.println("Uygun doktor veya ameliyathane eslesmesi bulunamadi.");
        } else {
            for (SurgerySchedule s : schedules) {
                System.out.println(s.toString());
            }
            System.out.println("\n[BILGI] Cizelgeleme tamamlandi ve 'data/schedules.txt' dosyasina kaydedildi.");
        }
    }

    private void addNewPatientMenu() {
        System.out.println("\n--- YENI HASTA KAYDI ---");
        String id = "PAT-" + (service.getPatients().size() + 1);
        System.out.print("TC Kimlik No (11 Hane): ");
        String tc = scanner.nextLine();
        System.out.print("Ad Soyad: ");
        String name = scanner.nextLine();
        System.out.print("Yas: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Triyaj Aciliyet Seviyesi (1: En Dusuk - 5: En Kritik): ");
        int triage = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Bekleme Suresi (Dakika): ");
        int wait = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ihtiyac Duyulan Brans (Orn: Kardiyoloji, Genel Cerrahi, Noroloji): ");
        String spec = scanner.nextLine();
        System.out.print("Ameliyat Gerekiyor mu? (E/H): ");
        boolean surgery = scanner.nextLine().trim().equalsIgnoreCase("E");

        Patient newPatient = new Patient(id, tc, name, age, triage, wait, spec, surgery);
        service.addPatient(newPatient);
        System.out.println("[BASARILI] Hasta eklendi ve dosyaya kaydedildi: " + newPatient.getFullName());
    }

    private void addNewDoctorMenu() {
        System.out.println("\n--- YENI DOKTOR KAYDI ---");
        String id = "DOC-" + (service.getDoctors().size() + 1);
        System.out.print("TC Kimlik No (11 Hane): ");
        String tc = scanner.nextLine();
        System.out.print("Ad Soyad: ");
        String name = scanner.nextLine();
        System.out.print("Yas: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Uzmanlik Bransi (Orn: Kardiyoloji, Genel Cerrahi, Noroloji): ");
        String spec = scanner.nextLine();

        Doctor newDoc = new Doctor(id, tc, name, age, spec);
        service.addDoctor(newDoc);
        System.out.println("[BASARILI] Doktor eklendi ve dosyaya kaydedildi: Dr. " + newDoc.getFullName());
    }

    private void addNewRoomMenu() {
        System.out.println("\n--- YENI AMELIYATHANE KAYDI ---");
        String id = "OR-" + (service.getRooms().size() + 1);
        System.out.print("Ameliyathane Adi (Orn: Ameliyathane C): ");
        String name = scanner.nextLine();
        System.out.print("Desteklenen Brans (Belirli bir brans veya GENEL): ");
        String spec = scanner.nextLine();

        OperatingRoom room = new OperatingRoom(id, name, spec);
        service.addOperatingRoom(room);
        System.out.println("[BASARILI] Ameliyathane eklendi ve dosyaya kaydedildi: " + room.getRoomName());
    }

    private void switchStrategyMenu() {
        System.out.println("\n--- ONCELIKLENDIRME STRATEJISI SECIMI ---");
        System.out.println("1. Acil Triyaj ve Cerrahi Oncelik Stratejisi (EmergencyTriageStrategy)");
        System.out.println("2. Standart Poliklinik Bekleme ve Yas Stratejisi (StandardPolyclinicStrategy)");
        System.out.print("Seciminiz (1 veya 2): ");
        String sub = scanner.nextLine().trim();

        if (sub.equals("1")) {
            service.changePriorityStrategy(new EmergencyTriageStrategy());
            System.out.println("[BILGI] Strateji degistirildi: " + service.getActiveStrategyName());
        } else if (sub.equals("2")) {
            service.changePriorityStrategy(new StandardPolyclinicStrategy());
            System.out.println("[BILGI] Strateji degistirildi: " + service.getActiveStrategyName());
        } else {
            System.out.println("[UYARI] Gecersiz secim.");
        }
    }

    public static void main(String[] args) {
        ConsoleMenuApp app = new ConsoleMenuApp();
        app.start();
    }
}