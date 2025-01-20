package ru.t1.java.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.java.demo.dto.TransactionDtoResponse;

/**
 * Сервисный слой для работы с транзакциями.
 */
public interface TransactionService {
    /**
     * Возвращает страницу с банковскими транзакциями.
     * @param pageable Страница с ее параметрами
     * @return Страница с банковскими транзакциями
     */
    Page<TransactionDtoResponse> getAllTransactions(Pageable pageable);

    /**
     * Возвращает транзакцию по ее идентификатору.
     * @param id Идентификатор транзакции
     * @return Информация о банковском счете
     */
    TransactionDtoResponse getTransaction(Long id);
}
