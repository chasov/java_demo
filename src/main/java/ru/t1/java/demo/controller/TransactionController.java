package ru.t1.java.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.java.demo.dto.TransactionDtoResponse;

/**
 * Контроллер для работы с транзакциями.
 */
public interface TransactionController {

    /**
     * Возвращает страницу со счетами клиентов.
     *
     * @param pageable Страница с параметрами
     * @return Страница со счетами
     */
    Page<TransactionDtoResponse> getAllTransactions(Pageable pageable);

    /**
     * Возвращает транзакцию по ее идентификатору.
     *
     * @param id Идентификатор транзакции
     * @return Информация о транзакции
     */
    TransactionDtoResponse getTransaction(Long id);
}
