#Задание №2
-----------------------------------


1. Изменен аспект @LogDataSourceError- теперь аспект может отсылать сообщение в топик t1_demo_metrics.
   В заголовке - тип ошибки: DATA_SOURCE;
   В случае, если отправка не удалась - пишет в БД.


2. Разработан аспект @Metric, принимающий в качестве значения время в миллисекундах.
   Если время работы метода превышает задаое значение, аспект отправляет сообщение в топик Kafka (t1_demo_metrics) c информацией о времени работы, имени метода и параметрах метода, если таковые имеются. В заголовке - тип ошибки METRICS.


3. Реализованы 2 консьюмера (KafkaAccountConsumer, KafkaTransactionConsumer), слушающих топики t1_demo_accounts и t1_demo_transactions. При получении сообщения сервис сохраняет транзакцию в БД.

Для проверки консьюмеров реализованы продюсеры аккаунтов и транзакций (KafkaAccountProducer, KafkaTransactionProducer).
Так же добавлены тестовые методы SendToKafka - для подстановки и проверки значений.