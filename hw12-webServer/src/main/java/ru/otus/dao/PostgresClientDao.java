package ru.otus.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.List;
import java.util.Optional;

public class PostgresClientDao implements ClientDao{
    private static final Logger logger = LoggerFactory.getLogger(PostgresClientDao.class);

    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private DbServiceClientImpl dbServiceClient;

    public PostgresClientDao() {
        Configuration configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        String dbUrl = configuration.getProperty("hibernate.connection.url");
        String dbUserName = configuration.getProperty("hibernate.connection.username");
        String dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        SessionFactory sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        TransactionManagerHibernate transactionManager = new TransactionManagerHibernate(sessionFactory);

        DataTemplateHibernate clientTemplate = new DataTemplateHibernate<>(Client.class);

        dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    @Override
    public Optional<Client> findById(long id) {
        return dbServiceClient.getClient(id);
    }

    @Override
    public List<Client> findAll() {
        return dbServiceClient.findAll();
    }

    @Override
    public Optional<Client> createClient(Client client) {
        return Optional.of(dbServiceClient.saveClient(client));
    }
}
