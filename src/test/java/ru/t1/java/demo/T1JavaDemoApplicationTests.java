package ru.t1.java.demo;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.hibernate.annotations.CurrentTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import ru.t1.java.demo.DataSourceErrorLog.DataSourceErrorLogRepository;
import ru.t1.java.demo.DataSourceErrorLog.component.DataSourceErrorLoggingAspect;
import ru.t1.java.demo.account.dto.AccountDto;
import ru.t1.java.demo.account.enums.AccountScoreType;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.service.AccountService;
import ru.t1.java.demo.generator.DataGenerator;
import ru.t1.java.demo.kafka.KafkaAccountConsumer;
import ru.t1.java.demo.kafka.KafkaTransactionConsumer;
import ru.t1.java.demo.metric.model.Metric;
import ru.t1.java.demo.metric.model.MetricAspect;
import ru.t1.java.demo.transaction.dto.TransactionDto;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.repository.TransactionRepository;
import ru.t1.java.demo.transaction.service.TransactionService;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableAspectJAutoProxy
class T1JavaDemoApplicationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DataSourceErrorLogRepository errorLogRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @InjectMocks
    private DataSourceErrorLoggingAspect dataSourceErrorLoggingAspect;

    @InjectMocks
    private MetricAspect metricAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private KafkaAccountConsumer kafkaAccountConsumer;

    @Mock
    private Acknowledgment acknowledgment;

    @BeforeEach
    void setUp() {
        errorLogRepository.deleteAll();
        transactionRepository.deleteAll();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount(){
        Account account = DataGenerator.generateAccount();
        account = accountService.createAccount(account);
        assertNotNull(account.getId());
    }

    @Test
    void testGetAccount(){
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(UUID.randomUUID()));
    }

    @Test
    void testAccountNullBalance() {
        Account account = new Account(AccountScoreType.DEBIT, null);
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(account));
    }

    @Test
    void updateAccount(){
        Account oldAccount = accountService.getAccountById(UUID.fromString("f25d34d0-01be-4462-b59d-aa5f68c8975b"));
        Account newAccount = new Account(AccountScoreType.CREDIT, BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> accountService.updateAccountById(oldAccount.getId(), newAccount));
    }

    @Test
    void testCreateTransaction(){
        Transaction transaction = DataGenerator.generateTransaction();
        transaction = transactionService.createTransaction(transaction);
        assertNotNull(transaction.getId());
    }

    @Test
    void testGetTransaction(){
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(UUID.randomUUID()));
    }

    @Test
    void testTransactionNullAmount() {
        Transaction transaction = new Transaction(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(transaction));
    }

    @Test
    public void testLogErrorSendToClientT1TopicSuccess() {
        Exception ex = new RuntimeException("Test exception");
        JoinPoint joinPoint = mock(JoinPoint.class);

        dataSourceErrorLoggingAspect.logErrorSendToClientT1Topic(joinPoint, ex);

        verify(kafkaTemplate, times(1)).send(any(Message.class));
        verify(dataSourceErrorLogRepository, never()).save(any());
    }

    @Test
    public void testLogErrorSendToClientT1TopicFailure() {
        Exception ex = new RuntimeException("Test exception");
        JoinPoint joinPoint = mock(JoinPoint.class);

        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(any(Message.class));

        dataSourceErrorLoggingAspect.logErrorSendToClientT1Topic(joinPoint, ex);

        verify(kafkaTemplate, times(1)).send(any(Message.class));
        verify(dataSourceErrorLogRepository, times(1)).save(any());
    }

    @Test
    void testMeasureExecutionTimeNoKafkaMessageSent() throws Throwable {
        Metric metric = mock(Metric.class);
        when(metric.value()).thenReturn(1L);
        when(joinPoint.proceed()).thenReturn(null);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(new Object());
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        metricAspect.measureExecutionTime(joinPoint, metric);

        verify(kafkaTemplate, never()).send(any(Message.class));
    }

    @Test
    void testListener_Success() {
        List<AccountDto> accountDtos = Collections.singletonList(new AccountDto(AccountScoreType.DEBIT, BigDecimal.TEN));
        String topic = "t1_demo_accounts";
        String key = "test-key";
        kafkaAccountConsumer.listener(accountDtos, acknowledgment, topic, key);

        verify(accountService, times(1)).save(anyList());
        verify(acknowledgment, times(1)).acknowledge();
    }
}
