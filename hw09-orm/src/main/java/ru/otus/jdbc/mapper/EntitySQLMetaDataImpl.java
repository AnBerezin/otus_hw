package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    EntityClassMetaData entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        List<Field> fields = entityClassMetaData.getAllFields();
        String fieldsStr = fields.stream()
                .map(field -> field.getName())
                .collect(Collectors.joining(","));
        return String.format("select %s from %s", fieldsStr, entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        List<Field> fields = entityClassMetaData.getAllFields();
        String fieldsStr = fields.stream()
                .map(field -> field.getName())
                .collect(Collectors.joining(","));
        return String.format("select %s from %s where %s = (?)", fieldsStr, entityClassMetaData.getName(),
                entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql(List<Field> fields) {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder placeHolders = new StringBuilder();
        List<Field> classFields = entityClassMetaData.getFieldsWithoutId();
        for (Field field : classFields) {
            if (fields.contains(field)) {
                columnNames.append(field.getName()).append(", ");
                placeHolders.append("?, ");
            }
        }
        columnNames.setLength(columnNames.length() - 2);
        placeHolders.setLength(placeHolders.length() - 2);

        return String.format("insert into %s (%s) values (%s)", entityClassMetaData.getName(), columnNames, placeHolders);
    }

    @Override
    public String getUpdateSql() {
        StringBuilder placeHolders = new StringBuilder();
        List<Field> fields = entityClassMetaData.getAllFields();
        for (Field field : fields) {
            placeHolders.append(field.getName()).append(" = ?, ");
        }
        placeHolders.setLength(placeHolders.length() - 2);
        return String.format("update %s set %s", entityClassMetaData.getName(), placeHolders);
    }

    @Override
    public Constructor getEntityConstructor() {
        return entityClassMetaData.getConstructor();
    }
}
