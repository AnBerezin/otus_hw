package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

/** Создает SQL - запросы */
public interface EntitySQLMetaData {
    String getSelectAllSql();

    String getSelectByIdSql();

    String getInsertSql();

    String getUpdateSql();
}
