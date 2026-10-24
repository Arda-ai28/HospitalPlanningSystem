package com.hospital.repository;

import java.util.List;
import java.util.function.Function;

public interface IRepository<T> {
    void saveAll(String filePath, List<T> items);
    List<T> loadAll(String filePath, Function<String, T> mapper);
}