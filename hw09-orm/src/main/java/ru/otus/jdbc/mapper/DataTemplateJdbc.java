package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final EntityClassMetaData<T> entityClassMetaData;

    private final Type type;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;

        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("Missing type parameter.");
        }
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createInstance(rs);
                }
                return null;
            } catch (SQLException | NoSuchMethodException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), null, rs -> {
            List<T> resultList = new ArrayList<>();
            try {
                while (rs.next()) {
                    resultList.add(createInstance(rs));
                }
            } catch (SQLException | NoSuchMethodException e) {
                throw new DataTemplateException(e);
            }
            return resultList;
        }).orElse(null);
    }

    @Override
    public long insert(Connection connection, T entity) {
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), getFieldValueAsParam(entity));
    }

    @Override
    public void update(Connection connection, T entity) {
        throw new UnsupportedOperationException();
    }

    private T createInstance(ResultSet rs) throws NoSuchMethodException {
        Constructor<T> constructor = getRawType().getDeclaredConstructor();
        try {
            T instance = constructor.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            Set<String> columnNames = new HashSet<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnLabel(i).toLowerCase());
            }

            for (Field field : entityClassMetaData.getAllFields()) {
                String fieldName = field.getName().toLowerCase();

                if (columnNames.contains(fieldName)) {
                    field.setAccessible(true);
                    Object value = rs.getObject(field.getName());
                    field.set(instance, value);
                }
            }

            return instance;
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> getFieldValueAsParam(T entity) {
        List<Object> fieldValues = new ArrayList<>();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean isIdField = false;
            field.setAccessible(true);
            try {
                for (var annotation : field.getAnnotations()) {
                    if (annotation instanceof TableId) {
                        isIdField = true;
                    }
                }
                if (!isIdField) {
                    fieldValues.add(field.get(entity));
                }
            } catch (IllegalAccessException e) {
                throw new DataTemplateException(e);
            }
        }
        return fieldValues;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getRawType() {
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else {
            throw new RuntimeException("Cannot determine raw type for: " + type);
        }
    }
}
