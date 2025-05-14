package ru.otus.dataprocessor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.otus.model.Measurement;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourcesFileLoader implements Loader {
    private String fileName;

    private final ObjectMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.mapper = JsonMapper.builder().build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        //var file = new File(fileName);
        Measurement[] measurements;
        try {
            measurements = mapper.readValue(file, Measurement[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //List<Measurement> results = Arrays.asList();
        return Arrays.asList(measurements);
    }
}
