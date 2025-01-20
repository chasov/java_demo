package ru.t1.java.demo.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.t1.java.demo.dto.AccountDtoRequest;
import ru.t1.java.demo.dto.AccountDtoResponse;

/**
 * Контроллер для работы с банковскими счетами.
 */
public interface AccountController {

    /**
     * Возвращает страницу со счетами клиентов
     * @param pageable Страница с параметрами
     * @return Страница со счетами
     */
     Page<AccountDtoResponse> getAllAccounts(Pageable pageable);

    /**
     * Возвращает банковский счет по его идентификатору
     * @param id Идентификатор счета
     * @return Информация о счете
     */
     AccountDtoResponse getAccount(Long id);

    /**
     * Создает новый аккаунт.
     * @param accountDtoRequest Параметры нового аккаунта
     * @return Идентификатор созданного аккаунта
     */
    Long createAccount(AccountDtoRequest accountDtoRequest);

}
