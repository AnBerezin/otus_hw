package ru.otus.jdbc.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityClassMetaDataImpl.class);
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private Field idField;
    private final List<Field> fields = new ArrayList<>();
    private final List<Field> fieldsWithoutId = new ArrayList<>();

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        try {
            this.constructor = clazz.getConstructor();
            discoveryClass();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void discoveryClass() throws NoSuchMethodException {
        for (var field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            log.info(field.getName());
            fields.add(field);
            for (var annotation : field.getAnnotations()) {
                log.info(annotation.toString());
                if (annotation instanceof TableId) {
                    this.idField = field;
                }
            }
            if (idField != field) {
                fieldsWithoutId.add(field);
            }
        }
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return fields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
