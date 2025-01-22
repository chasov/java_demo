package ru.t1.java.demo.util;


import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.model.Transactional;

@Component
public class TransactionalMapper {

    public Transactional toEntity(TransactionalDTO transactionalDTO) {
        return Transactional.builder()
                .priceTransactional(transactionalDTO.getPriceTransactional())
                .timeTransactional(transactionalDTO.getTimeTransactional())
                .build();
    }

    public TransactionalDTO toDTO(Transactional transactional) {
        return TransactionalDTO.builder()
                .id(transactional.getId())
                .priceTransactional(transactional.getPriceTransactional())
                .timeTransactional(transactional.getTimeTransactional())
                .build();
    }
}
