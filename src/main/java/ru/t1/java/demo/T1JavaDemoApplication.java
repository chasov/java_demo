package ru.t1.java.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.t1.java.demo")
public class T1JavaDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(T1JavaDemoApplication.class, args);
    }
}
