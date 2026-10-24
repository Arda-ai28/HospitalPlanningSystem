package com.hospital.model;

public abstract class Person extends BaseEntity {
    private String tcNo;
    private String fullName;
    private int age;

    public Person(String id, String tcNo, String fullName, int age) {
        super(id);
        setTcNo(tcNo);
        setFullName(fullName);
        setAge(age);
    }

    public String getTcNo() {
        return tcNo;
    }

    public void setTcNo(String tcNo) {
        if (tcNo == null || tcNo.trim().length() != 11) {
            throw new IllegalArgumentException("TC Kimlik No 11 haneli olmalidir!");
        }
        this.tcNo = tcNo.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ad Soyad bos olamaz!");
        }
        this.fullName = fullName.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0 || age > 130) {
            throw new IllegalArgumentException("Gecersiz yas degeri: " + age);
        }
        this.age = age;
    }

    // Alt sınıfların kendi rol özetini döndürmesi için soyut metot
    public abstract String getRoleSummary();
}