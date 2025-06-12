package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.HomeWork;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private static final Logger log = LoggerFactory.getLogger(DataTemplateJdbc.class);

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return (T) createInstance(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long insert(Connection connection, T client) {
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(getNonEmptyFields(client)), getFieldValueAsParam(client));
    }

    @Override
    public void update(Connection connection, T client) {
        throw new UnsupportedOperationException();
    }

    private T createInstance(ResultSet rs) {
        Constructor constructor = entitySQLMetaData.getEntityConstructor();
        try {
            int paramCount = constructor.getParameterCount();
            Object[] constructorArgs = new Object[paramCount];
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 0; i < paramCount; i++) {
                if (i < columnCount) {
                    Object value = rs.getObject(i + 1);
                    constructorArgs[i] = value;
                } else {
                    constructorArgs[i] = null;
                }
            }
            return (T) constructor.newInstance(constructorArgs);
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Field> getNonEmptyFields (T client) {
        List<Field> fieldsWithValue = new ArrayList<>();
        Field[] fields = client.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(client) != null) {
                    fieldsWithValue.add(field);
                }
            } catch (IllegalAccessException e) {
                log.error("Ошибка доступа к полю: {}", field.getName());
                e.printStackTrace();
            }
        }
        return fieldsWithValue;
    }

    private List<Object> getFieldValueAsParam(T client) {
        List<Object> fieldValues = new ArrayList<>();
        Field[] fields = client.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(client) != null) {
                    fieldValues.add(field.get(client));
                }
            } catch (IllegalAccessException e) {
                log.error("Ошибка доступа к полю: {}", field.getName());
                e.printStackTrace();
            }
        }
        return fieldValues;
    }
}
