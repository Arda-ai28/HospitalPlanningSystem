package com.hospital.model;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    private String id;
    private LocalDateTime createdAt;

    public BaseEntity(String id) {
        setId(id);
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    // Kapsülleme (Encapsulation): Boş ID girilmesini engeller
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID bos olamaz!");
        }
        this.id = id.trim();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Soyutlama (Abstraction): Her alt sınıf dosyaya nasıl yazılacağını kendi belirler (Çok Biçimlilik)
    public abstract String toFileFormat();
}