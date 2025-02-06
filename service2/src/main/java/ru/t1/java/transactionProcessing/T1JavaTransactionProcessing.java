package ru.t1.java.transactionProcessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class T1JavaTransactionProcessing {
    public static void main(String[] args) {
        SpringApplication.run(T1JavaTransactionProcessing.class, args);
    }
}
