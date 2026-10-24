package com.hospital.repository;

import com.hospital.model.BaseEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileRepositoryManager<T extends BaseEntity> implements IRepository<T> {

    // Singleton Tasarım Kalıbı: Tekil örnek (Instance)
    private static FileRepositoryManager<?> instance;

    private FileRepositoryManager() {
        // Dışarıdan 'new' ile üretilmeyi engeller (Singleton)
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T extends BaseEntity> FileRepositoryManager<T> getInstance() {
        if (instance == null) {
            instance = new FileRepositoryManager<>();
        }
        return (FileRepositoryManager<T>) instance;
    }

    @Override
    public void saveAll(String filePath, List<T> items) {
        try {
            Path path = Paths.get(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (T item : items) {
                    writer.write(item.toFileFormat());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya kaydetme hatasi (" + filePath + "): " + e.getMessage());
        }
    }

    @Override
    public List<T> loadAll(String filePath, Function<String, T> mapper) {
        List<T> list = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return list; // Dosya henüz yoksa boş liste dön
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(mapper.apply(line.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okuma hatasi (" + filePath + "): " + e.getMessage());
        }
        return list;
    }
}