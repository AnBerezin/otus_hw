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
    private Constructor<T> constructor;
    private Field idField;
    private final List<Field> fields = new ArrayList<>();

    public EntityClassMetaDataImpl(Class clazz) {
        this.clazz = clazz;
        //this.clazz = (Class<T>) clazz;
        try {
            discoveryClass();
        } catch (NoSuchMethodException e) {
            log.error("Method not found for Class {}", clazz.getCanonicalName());
            log.error(e.toString());
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
        }

        Class<?>[] parameterTypes = new Class[fields.size()];

        for (int i = 0; i < fields.size(); i++) {
            parameterTypes[i] = fields.get(i).getType();
        }

        this.constructor = clazz.getConstructor(parameterTypes);
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor getConstructor() {
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
        return fields.stream()
                .filter(field -> !field.equals(this.idField))
                .toList();
    }
}
