package ru.t1.java.demo.transaction.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.t1.java.demo.account.enums.AccountScoreType;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum TransactionStatus {

    ACCEPTED("Принято", 0), REJECTED("Отклонено", 1), BLOCKED("Заблокировано", 2), CANCELLED("Отменено", 3), REQUESTED("Запрошено", 4);

    private final String title;

    private final int value;

    @JsonCreator
    static AccountScoreType findValue(@JsonProperty("value") String value) {
        try {
            int x = Integer.parseInt(value);
            return Arrays.stream(AccountScoreType.values()).filter(v -> v.getValue() == x).findFirst().get();
        } catch (Exception s) {
            return AccountScoreType.valueOf(value);
        }
    }

}
