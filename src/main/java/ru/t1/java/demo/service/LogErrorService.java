package ru.t1.java.demo.service;


public interface LogErrorService {

    /**
     * Логирует сообщение об ошибке
     *
     * @param throwable Исключение
     * @param methodSignature Имя сигнатруы метода
     */
    void logError(Throwable throwable, String methodSignature);
}
