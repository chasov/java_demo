package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.AcceptedTransactionMessage;
import ru.t1.java.demo.dto.IncomingResultTransactionDto;
import ru.t1.java.demo.dto.IncomingTransactionDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.producer.TransactionProducer;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionProducer transactionProducer;
    private final TransactionMapper transactionMapper;

    @Transactional
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public Transaction saveTransaction(TransactionDto transactionDto) {
        return transactionRepository.save(transactionMapper.toEntity(transactionDto));
    }

    @Transactional
    public void processTransaction(IncomingTransactionDto incomingTransactionDto) {
        Transaction transaction = transactionMapper.toEntity(incomingTransactionDto);
        log.info("Transaction timestamp {}", transaction.getTimestamp());
        Account account = transaction.getAccount();
        log.info("Transaction account is {}", account.getId());

        if (Account.AccountStatus.OPEN.equals(account.getStatus())) {// проверяет статус счета: если статус OPEN,

            transaction.setStatus(Transaction.TransactionStatus.REQUESTED); // сохраняет транзакцию в БД со статусом REQUESTED
            log.info("Transaction {} requested", transaction.getId());
            transactionRepository.save(transaction);
            log.info("Transaction {} saved", transaction.getId());

            Double balanceBefore = accountService.findAccountById(account.getId()).get().getBalance();
            accountService.changeAmount(account, transaction.getAmount()); // изменяет счет клиента на сумму транзакции
            log.info("Account {} changed amount from {} to {}", transaction.getId(), balanceBefore, account.getBalance());

            AcceptedTransactionMessage acceptedTransactionMessage = transactionMapper.toAcceptedTransaction(transaction);

            transactionProducer.sendTransactionAccept(acceptedTransactionMessage); // отправляем

        }
    }

    public void processTransactionResult(IncomingResultTransactionDto transactionDto) {
        Transaction transaction = transactionRepository.getReferenceById(transactionDto.transactionId());
        switch (transaction.getStatus()) {
            case ACCEPTED:
                log.info("Transaction {} accepted", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.ACCEPTED);
                transactionRepository.save(transaction);
                break;
            case BLOCKED:
                log.info("Transaction {} blocked", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.BLOCKED);
                accountService.changeStatus(transaction.getAccount(), "BLOCKED");
                accountService.correctAmount(transaction.getAccount(), transaction.getAmount());
                transactionRepository.save(transaction);
                break;
            case REJECTED:
                log.info("Transaction {} rejected", transaction.getId());
                transaction.setStatus(Transaction.TransactionStatus.REJECTED);
                accountService.changeAmount(transaction.getAccount(), transaction.getAmount());
                break;
        }
    }
}


//    @PostConstruct
//    public void initMockData() {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream inputStream = getClass().getResourceAsStream("/MOCK_TRANSACTIONS.json");
//            if (inputStream == null) {
//                throw new IllegalStateException("MOCK_TRANSACTIONS.json not found");
//            }
//            List<TransactionDto> transactions = mapper.readValue(inputStream, new TypeReference<>() {});
//            transactions.forEach(transactionsDto -> transactionRepository.save(TransactionMapper.toEntity(transactionsDto)));
//            System.out.println("Mock data initialized successfully.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Failed to initialize mock data: " + e.getMessage());
//        }
//    }
