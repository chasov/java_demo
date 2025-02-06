package ru.t1.java.demo.util;


import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.model.Transactional;


public class TransactionalMapper {

    private TransactionalMapper() {

    }

    public static Transactional toEntity(TransactionalDTO transactionalDTO) {
        return Transactional.builder()
                .account(AccountMapper.toEntity(transactionalDTO.getAccount()))
                .priceTransactional(transactionalDTO.getPriceTransactional())
                .transactionalStatus(transactionalDTO.getTransactionalStatus())
                .timeTransactional(transactionalDTO.getTimeTransactional())
                .timestamp(transactionalDTO.getTimestamp())
                .build();
    }

    public static TransactionalDTO toDTO(Transactional transactional) {
        return TransactionalDTO.builder()
                .transactionalId(transactional.getTransactionalId())
                .account(AccountMapper.toDTO(transactional.getAccount()))
                .priceTransactional(transactional.getPriceTransactional())
                .transactionalStatus(transactional.getTransactionalStatus())
                .timeTransactional(transactional.getTimeTransactional())
                .timestamp(transactional.getTimestamp())
                .build();
    }
}
