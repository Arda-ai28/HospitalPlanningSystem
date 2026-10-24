#  Hastane Planlama ve Çizelgeleme Sistemi (Hospital Planning System)

Bu proje, **Nesneye Yönelik Programlama (OOP)** dersi kapsamında; acil ve poliklinik hastalarının dinamik olarak önceliklendirilmesi, doktor çalışma saatleri ve ameliyathane uygunluklarının çakışmasız olarak çizelgelenmesi amacıyla geliştirilmiş konsol tabanlı bir Java uygulamasıdır.

---

##  Karşılanan Ödev Kriterleri ve Mimari Yapı

### 1. Nesneye Yönelik Programlama (OOP) İlkeleri
* **Kalıtım (Inheritance):** `BaseEntity` -> `Person` -> `Doctor` ve `Patient` şeklinde çok katmanlı kalıtım hiyerarşisi kurulmuştur.
* **Soyutlama (Abstraction):** `BaseEntity` ve `Person` sınıfları `abstract` olarak tasarlanmış; `toFileFormat()` ve `getRoleSummary()` soyut metotları alt sınıflarda zorunlu kılınmıştır.
* **Çok Biçimlilik (Polymorphism):** `PriorityStrategy` arayüzü üzerinden farklı puanlama stratejilerinin çalışma zamanında değiştirilmesi ve `Person` referansı üzerinden tüm hastane personelinin/hastalarının listelenmesi sağlanmıştır.
* **Kapsülleme (Encapsulation):** Tüm model alanları `private` tanımlanmış; TC Kimlik No, yaş, çalışma saatleri (08:00-17:00) ve Triyaj Seviyesi (1-5) kuralları `setter` metotları içinde doğrulamalarla (`IllegalArgumentException`) korunmuştur.

### 2. Uygulanan SOLID Prensipleri (5/5)
1. **Single Responsibility (Tek Sorumluluk):** Veri modelleri (`model`), dosya işlemleri (`FileRepositoryManager`), algoritmalar (`HospitalSchedulerAlgorithm`) ve iş kuralları (`HospitalPlanningService`) tamamen ayrı sınıflara bölünmüştür.
2. **Open/Closed (Açık/Kapalı):** Yeni bir hasta önceliklendirme kuralı eklemek için mevcut algoritma kodları değiştirilmez; `PriorityStrategy` arayüzünü uygulayan yeni bir sınıf eklenmesi yeterlidir.
3. **Liskov Substitution (Liskov Yerine Geçme):** `Person` üst sınıfı beklenen her yerde `Doctor` ve `Patient` alt sınıfları davranış bozukluğu olmadan kullanılabilmektedir.
4. **Interface Segregation (Arayüz Ayrımı):** Veri erişim işlemleri `IRepository<T>`, strateji işlemleri `PriorityStrategy` arayüzleriyle ayrıştırılmıştır.
5. **Dependency Inversion (Bağımlılığın Tersine Çevrilmesi):** `HospitalPlanningService` sınıfı somut dosya kayıt sınıfına değil, soyut `IRepository<T>` arayüzüne bağımlıdır (Constructor Injection).

### 3. Kullanılan Tasarım Kalıpları (Design Patterns)
1. **Strategy Pattern:** `PriorityStrategy` arayüzü ve bunu uygulayan `EmergencyTriageStrategy` ile `StandardPolyclinicStrategy` sınıfları sayesinde hasta öncelik puanı hesaplama algoritması çalışma zamanında dinamik olarak değiştirilebilmektedir.
2. **Singleton Pattern:** `FileRepositoryManager` sınıfı uygulama boyunca tek bir örnek (`getInstance()`) üzerinden çalışarak dosya okuma/yazma çakışmalarını önler.
3. **Factory Pattern:** `EntityFactory` sınıfı, `.txt` dosyalarından okunan ham metin satırlarını ilgili `Doctor`, `Patient` ve `OperatingRoom` nesnelerine dönüştürür.

### 4. Algoritmalar
* **Dinamik Triyaj Önceliklendirme (Max-Heap / PriorityQueue):** Hastaların aciliyet kodu, bekleme süresi (Starvation/Açlık önleme), yaş riski ve cerrahi müdahale gereksinimine göre ağırlıklı puan hesaplanır ve $O(N \log N)$ karmaşıklıkta sıralanır.
* **Greedy Zaman Çizelgeleme (Interval Scheduling):** Öncelik kuyruğundan çıkan hastalar için 08:00 - 17:00 mesai saatleri arasında hem branşı uygun doktorun hem de ameliyathanenin aynı anda müsait olduğu en erken saat dilimi açgözlü (greedy) yaklaşımla çakışmasız olarak atanır.

---

##  UML Sınıf Diyagramı

```mermaid
classDiagram
    direction TB

    class BaseEntity {
        <<abstract>>
        -String id
        -LocalDateTime createdAt
        +getId() String
        +setId(String id) void
        +toFileFormat()* String
    }

    class Person {
        <<abstract>>
        -String tcNo
        -String fullName
        -int age
        +getRoleSummary()* String
    }

    class Doctor {
        -String specialty
        -Set~Integer~ busyHours
        +isAvailableAt(int hour) boolean
        +bookHour(int hour) void
        +getRoleSummary() String
        +toFileFormat() String
    }

    class Patient {
        -int triageLevel
        -int waitingMinutes
        -String requiredSpecialty
        -boolean requiresSurgery
        -double calculatedPriorityScore
        +setTriageLevel(int level) void
        +getRoleSummary() String
        +toFileFormat() String
    }

    class OperatingRoom {
        -String roomName
        -String supportedSpecialty
        -Set~Integer~ reservedHours
        +isAvailableAt(int hour) boolean
        +reserveHour(int hour) void
        +toFileFormat() String
    }

    class SurgerySchedule {
        -Patient patient
        -Doctor doctor
        -OperatingRoom operatingRoom
        -int scheduledHour
        +toFileFormat() String
    }

    class PriorityStrategy {
        <<interface>>
        +calculateScore(Patient patient) double
        +getStrategyName() String
    }

    class EmergencyTriageStrategy {
        +calculateScore(Patient patient) double
        +getStrategyName() String
    }

    class StandardPolyclinicStrategy {
        +calculateScore(Patient patient) double
        +getStrategyName() String
    }

    class EntityFactory {
        +createDoctorFromLine(String line)$ Doctor
        +createPatientFromLine(String line)$ Patient
        +createRoomFromLine(String line)$ OperatingRoom
    }

    class IRepository~T~ {
        <<interface>>
        +saveAll(String filePath, List~T~ items) void
        +loadAll(String filePath, Function mapper) List~T~
    }

    class FileRepositoryManager~T~ {
        -FileRepositoryManager instance$
        +getInstance()$ FileRepositoryManager
        +saveAll(String filePath, List~T~ items) void
        +loadAll(String filePath, Function mapper) List~T~
    }

    class HospitalSchedulerAlgorithm {
        -PriorityStrategy strategy
        +prioritizePatients(List~Patient~ patients) List~Patient~
        +schedulePatients(List~Patient~, List~Doctor~, List~OperatingRoom~) List~SurgerySchedule~
    }

    class HospitalPlanningService {
        -IRepository~Doctor~ doctorRepo
        -IRepository~Patient~ patientRepo
        -IRepository~OperatingRoom~ roomRepo
        -IRepository~SurgerySchedule~ scheduleRepo
        -HospitalSchedulerAlgorithm schedulerAlgorithm
        +initializeSystemData() void
        +runSchedulingAlgorithm() List~SurgerySchedule~
    }

    class ConsoleMenuApp {
        -HospitalPlanningService service
        +start() void
        +main(String[] args)$ void
    }

    BaseEntity <|-- Person
    BaseEntity <|-- OperatingRoom
    BaseEntity <|-- SurgerySchedule
    Person <|-- Doctor
    Person <|-- Patient

    PriorityStrategy <|.. EmergencyTriageStrategy
    PriorityStrategy <|.. StandardPolyclinicStrategy
    IRepository <|.. FileRepositoryManager

    SurgerySchedule --> Patient
    SurgerySchedule --> Doctor
    SurgerySchedule --> OperatingRoom

    HospitalSchedulerAlgorithm --> PriorityStrategy
    HospitalPlanningService --> IRepository
    HospitalPlanningService --> HospitalSchedulerAlgorithm
    HospitalPlanningService ..> EntityFactory
    ConsoleMenuApp --> HospitalPlanningService
```

---

##  Kurulum ve Çalıştırma

1. Projeyi klonlayın:
   ```bash
   git clone [https://github.com/KULLANICI_ADINIZ/HospitalPlanningSystem.git](https://github.com/KULLANICI_ADINIZ/HospitalPlanningSystem.git)
   ```
2. IntelliJ IDEA ile projeyi Maven projesi olarak açın.
3. Uygulamayı başlatmak için `src/main/java/com/hospital/ui/ConsoleMenuApp.java` sınıfını çalıştırın.
4. 8 Birim testini çalıştırmak için `src/test/java/com/hospital/HospitalPlanningSystemTest.java` sınıfına sağ tıklayıp **Run** seçeneğine basın veya terminalden `mvn test` komutunu çalıştırın.