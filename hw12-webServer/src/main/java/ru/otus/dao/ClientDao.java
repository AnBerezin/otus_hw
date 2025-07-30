package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import ru.otus.crm.model.Client;

public interface ClientDao {
    Optional<Client> findById(long id);

    List<Client> findAll();

    Optional<Client> createClient(Client client);
}
