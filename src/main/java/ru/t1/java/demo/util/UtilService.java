package ru.t1.java.demo.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UtilService {

    public String generateUniqueId(Set<String> existingEntityIds) {
        String entityId;
        do {
            entityId = UUID.randomUUID().toString();
        } while (existingEntityIds.contains(entityId));

        return entityId;
    }
}
