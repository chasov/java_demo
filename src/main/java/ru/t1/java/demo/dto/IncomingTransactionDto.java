package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record IncomingTransactionDto (Double amount,
                                      Long client_id,
                                      @JsonFormat(pattern = "dd/MM/yyyy")
                                      Date transaction_time,
                                      String status,
                                      Long account_id) {}
