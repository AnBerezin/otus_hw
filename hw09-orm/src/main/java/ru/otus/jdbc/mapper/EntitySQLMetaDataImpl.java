package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final EntityClassMetaData<T> entityClassMetaData;

    private String selectAllSql;
    private String selectByIdSql;
    private String insertSql;
    private String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;

        createSelectAllSql();
        createSelectById();
        createInsertSql();
        createUpdateSql();
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }

    /*@Override
    public Constructor getEntityConstructor() {
        return entityClassMetaData.getConstructor();
    }*/

    private void createSelectAllSql() {
        List<Field> fields = entityClassMetaData.getAllFields();
        String fieldsStr = fields.stream()
                .map(field -> field.getName())
                .collect(Collectors.joining(","));
        this.selectAllSql = String.format("select %s from %s", fieldsStr, entityClassMetaData.getName());
        ;
    }

    private void createSelectById() {
        List<Field> fields = entityClassMetaData.getAllFields();
        String fieldsStr = fields.stream()
                .map(field -> field.getName())
                .collect(Collectors.joining(","));
        this.selectByIdSql = String.format("select %s from %s where %s = (?)", fieldsStr, entityClassMetaData.getName(),
                entityClassMetaData.getIdField().getName());
    }

    private void createInsertSql() {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder placeHolders = new StringBuilder();
        List<Field> classFields = entityClassMetaData.getFieldsWithoutId();
        for (Field field : classFields) {
            columnNames.append(field.getName()).append(", ");
            placeHolders.append("?, ");
        }
        columnNames.setLength(columnNames.length() - 2);
        placeHolders.setLength(placeHolders.length() - 2);

        this.insertSql = String.format("insert into %s (%s) values (%s)", entityClassMetaData.getName(), columnNames, placeHolders);
    }

    private void createUpdateSql() {
        StringBuilder placeHolders = new StringBuilder();
        List<Field> fields = entityClassMetaData.getAllFields();
        for (Field field : fields) {
            placeHolders.append(field.getName()).append(" = ?, ");
        }
        placeHolders.setLength(placeHolders.length() - 2);
        this.updateSql = String.format("update %s set %s", entityClassMetaData.getName(), placeHolders);
    }
}
