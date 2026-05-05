package com.infrarecord;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9999",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class InfraRecordApplicationTests {

    @Test
    void contextLoads() {
    }
}
