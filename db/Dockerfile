FROM postgres:16

        # Копируем скрипт инициализации (если нужно)
COPY createDB.sql /docker-entrypoint-initdb.d/

        # Устанавливаем переменные окружения
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=1
