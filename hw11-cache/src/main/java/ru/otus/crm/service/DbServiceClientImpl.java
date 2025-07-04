package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final HwCache<String, Client> cache;

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate, HwCache<String, Client> cache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.cache = cache;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
                putToCache(savedClient);
                return savedClient;
            }
            var savedClient = clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", savedClient);
            putToCache(savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {

            var clientOptional = Optional.ofNullable(cache.get(String.valueOf(id)));
            if (clientOptional.isPresent()) {
                return clientOptional;
            }
            clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (clientOptional.isPresent()) {
                putToCache(clientOptional.get());
            }
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            putListClientsToCache(clientList);
            return clientList;
        });
    }

    private void putListClientsToCache(List<Client> client) {
        client.stream().forEach(this::putToCache);
    }

    private void putToCache(Client client) {
        String weekId = client.getId().toString();
        cache.put(weekId, client);
        weekId = null;
    }
}
