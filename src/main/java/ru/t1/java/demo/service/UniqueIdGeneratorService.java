package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.EntityType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UniqueIdGeneratorService {

    AccountRepository accountRepository;
    ClientRepository clientRepository;
    TransactionRepository transactionRepository;

    public UniqueIdGeneratorService(AccountRepository accountRepository,
                             ClientRepository clientRepository,
                             TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
    }



    private final Map<EntityType, Long> entityCounters = new EnumMap<>(EntityType.class);

    // Пусть возьмет данные из БД только при запуске приложения.
   @PostConstruct
    @Transactional
    public void initializeCounters() {
        entityCounters.put(EntityType.ACCOUNT, getRowCount(EntityType.ACCOUNT));
        entityCounters.put(EntityType.TRANSACTION, getRowCount(EntityType.TRANSACTION));
        entityCounters.put(EntityType.CLIENT, getRowCount(EntityType.CLIENT));
    }

    public String generateId(EntityType entityType) {
        String prefix;
        if (Objects.requireNonNull(entityType) == EntityType.ACCOUNT) {
            prefix = "ACC-";
        } else if (entityType == EntityType.TRANSACTION) {
            prefix = "TRX-";
        } else if (entityType == EntityType.CLIENT) {
            prefix = "CLT-";
        } else {
            throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }

        long currentCount = entityCounters.get(entityType) + 1;
        entityCounters.put(entityType, currentCount);

        String formattedIdNumber = String.format("%08d", currentCount);
        return prefix + formattedIdNumber;
    }

    private long getRowCount(EntityType entityType) {

        if (Objects.requireNonNull(entityType) == EntityType.ACCOUNT) {
            return accountRepository.count();
        } else if (entityType == EntityType.TRANSACTION) {
            return transactionRepository.count();
        } else if (entityType == EntityType.CLIENT) {
            return clientRepository.count();
        } else {
            throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }

    }
}