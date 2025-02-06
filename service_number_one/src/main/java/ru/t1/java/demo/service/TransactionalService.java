package ru.t1.java.demo.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.exception.TransactionalException;
import ru.t1.java.demo.model.Transactional;
import ru.t1.java.demo.model.enums.TransactionalStatus;
import ru.t1.java.demo.repository.TransactionalRepository;
import ru.t1.java.demo.util.TransactionalMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@LogDataSourceError
@Slf4j
public class TransactionalService {

    private final TransactionalRepository transactionalRepository;

    private static final String TRANSACTIONAL_NOT_FOUND_WITH_ID = "Transactional not found with id: ";


    public List<TransactionalDTO> getAllTransactional(int page, int size) {
        log.info("Получение всех транзакций - Страница: {}, Размер: {}", page, size);
        return transactionalRepository.findAll(PageRequest.of(page - 1, size)).stream()
                .map(TransactionalMapper::toDTO)
                .toList();
    }

    public TransactionalDTO getTransactional(UUID id) {
        log.info("Получение транзакции с ID: {}", id);
        Transactional transactional = findById(id);
        return TransactionalMapper.toDTO(transactional);
    }

    public Transactional addTransactional(TransactionalDTO transactionalDTO) {
        Transactional transactional = TransactionalMapper.toEntity(transactionalDTO);
        transactional.setTransactionalStatus(TransactionalStatus.REQUESTED);
        transactional.setTimestamp(LocalDateTime.now());
        transactional.setTimeTransactional(System.currentTimeMillis() - transactional.getTimeTransactional());

        Transactional save = transactionalRepository.save(transactional);
        log.info("Добавлена новая транзакция с ID: {}", save.getTransactionalId());
        return save;


    }

    @Metric
    public Transactional patchTransactional(UUID id, TransactionalStatus transactionalStatus) {
        log.info("Обновление транзакции с ID: {}", id);
        Transactional transactional = findById(id);
        transactional.setTransactionalStatus(transactionalStatus);
        Transactional save = transactionalRepository.save(transactional);
        log.info("Транзакция с ID: {} обновлена", id);
        return save;
    }

    public void deleteTransactional(UUID id) {
        log.info("Попытка удалить транзакцию с ID: {}", id);
        if (!transactionalRepository.existsById(id)) {
            throw new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id);
        }
        transactionalRepository.deleteById(id);
        log.info("Транзакция с ID: {} удалена", id);
    }

    private Transactional findById(UUID id) {
        return transactionalRepository.findById(id)
                .orElseThrow(() -> new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id));
    }
}
