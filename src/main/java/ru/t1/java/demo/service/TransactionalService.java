package ru.t1.java.demo.service;


import lombok.RequiredArgsConstructor;
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
public class TransactionalService {

    private final TransactionalRepository transactionalRepository;

    private final TransactionalMapper transactionalMapper;
    private static final String TRANSACTIONAL_NOT_FOUND_WITH_ID = "Transactional not found with id: ";


    public List<TransactionalDTO> getAllTransactional(int page, int size) {
        return transactionalRepository.findAll(PageRequest.of(page - 1, size)).stream()
                .map(transactionalMapper::toDTO)
                .toList();
    }

    public TransactionalDTO getTransactional(Long id) {
        Transactional transactional = transactionalRepository
                .findById(id).orElseThrow(() -> new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id));
        return transactionalMapper.toDTO(transactional);
    }

    public Long addTransactional(TransactionalRequestDTO transactionalRequestDTO) {
        Transactional transactional = new Transactional(
                transactionalRequestDTO.getPriceTransactional(),
                transactionalRequestDTO.getTimeTransactional());
        return transactionalRepository.save(transactional).getId();
    }

    public void patchTransactional(Long id, TransactionalRequestDTO transactionalRequestDTO) {
        Transactional transactional = transactionalRepository.findById(id)
                .orElseThrow(() -> new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id));

        if (transactionalRequestDTO.getPriceTransactional() != null) {
            transactional.setPriceTransactional(transactionalRequestDTO.getPriceTransactional());
        }

        if (transactionalRequestDTO.getTimeTransactional() != null) {
            transactional.setTimeTransactional(transactionalRequestDTO.getTimeTransactional());
        }

        transactionalRepository.save(transactional);
    }

    public void deleteTransactional(Long id) {
        if (!transactionalRepository.existsById(id)) {
            throw new TransactionalException(TRANSACTIONAL_NOT_FOUND_WITH_ID + id);
        }
        transactionalRepository.deleteById(id);
    }
}
