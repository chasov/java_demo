package ru.t1.java.demo.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.dto.TransactionalRequestDTO;
import ru.t1.java.demo.exception.TransactionalException;
import ru.t1.java.demo.model.Transactional;
import ru.t1.java.demo.repository.TransactionalRepository;
import ru.t1.java.demo.util.TransactionalMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@LogDataSourceError
@Slf4j
public class TransactionalService {

    private final TransactionalRepository transactionalRepository;

    private final TransactionalMapper transactionalMapper;
    private static final String TRANSACTIONAL_NOT_FOUND_WITH_ID = "Transactional not found with id: ";


    public List<TransactionalDTO> getAllTransactional(int page, int size) {
        log.info("Получение всех транзакций - Страница: {}, Размер: {}", page, size);
        return transactionalRepository.findAll(PageRequest.of(page - 1, size)).stream()
                .map(transactionalMapper::toDTO)
                .toList();
    }

    public TransactionalDTO getTransactional(Long id) {
        log.info("Получение транзакции с ID: {}", id);
        Transactional transactional = transactionalRepository
                .findById(id).orElseThrow(() -> new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id));
        return transactionalMapper.toDTO(transactional);
    }

    public Long addTransactional(TransactionalRequestDTO transactionalRequestDTO) {
        log.info("Добавление новой транзакции с ценой: {} и временем: {}",
                transactionalRequestDTO.getPriceTransactional(),
                transactionalRequestDTO.getTimeTransactional());

        Transactional transactional = new Transactional(
                transactionalRequestDTO.getPriceTransactional(),
                transactionalRequestDTO.getTimeTransactional());

        Long transactionalId = transactionalRepository.save(transactional).getId();
        log.info("Добавлена новая транзакция с ID: {}", transactionalId);
        return transactionalId;
    }

    public void patchTransactional(Long id, TransactionalRequestDTO transactionalRequestDTO) {
        log.info("Обновление транзакции с ID: {}", id);
        Transactional transactional = transactionalRepository.findById(id)
                .orElseThrow(() -> new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id));

        if (transactionalRequestDTO.getPriceTransactional() != null) {
            transactional.setPriceTransactional(transactionalRequestDTO.getPriceTransactional());
        }

        if (transactionalRequestDTO.getTimeTransactional() != null) {
            transactional.setTimeTransactional(transactionalRequestDTO.getTimeTransactional());
        }

        transactionalRepository.save(transactional);
        log.info("Транзакция с ID: {} обновлена", id);
    }

    public void deleteTransactional(Long id) {
        log.info("Попытка удалить транзакцию с ID: {}", id);
        if (!transactionalRepository.existsById(id)) {
            throw new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id);
        }
        transactionalRepository.deleteById(id);
        log.info("Транзакция с ID: {} удалена", id);
    }
}
