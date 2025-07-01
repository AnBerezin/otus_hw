package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        var cache = new MyCache<String, Client>();


        List<Long> clientsId = new ArrayList<>();

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, cache);
        for (var idx = 0; idx < 10; idx++) {
            var client = dbServiceClient.saveClient(new Client("Client_" + idx));
            clientsId.add(client.getId());
        }

        //Первый запрос
        long startTime = System.currentTimeMillis();
        clientsId.stream().forEach(id -> {
            dbServiceClient.getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + id));
        });
        long delta = System.currentTimeMillis() - startTime;
        log.info("Время выполнения первого запроса {}", delta);

        sessionFactory.getCache().evictAll();

        //Второй запрос
        startTime = System.currentTimeMillis();
        clientsId.stream().forEach(id -> {
            dbServiceClient.getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + id));
        });
        delta = System.currentTimeMillis() - startTime;
        log.info("Время выполнения второго запроса {}", delta);


        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Третий запрос
        startTime = System.currentTimeMillis();
        clientsId.stream().forEach(id -> {
            dbServiceClient.getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + id));
        });
        delta = System.currentTimeMillis() - startTime;
        log.info("Время выполнения третьего запроса {}", delta);
    }
}
