package ru.t1.java.demo.account.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum AccountStatus {

    ARRESTED("Арестован", 0), BLOCKED("Заблокирован", 1), CLOSED("Закрыт", 2), OPEN("Открыт", 1);

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
