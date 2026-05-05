package com.infrarecord.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9999",
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getSummary_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClusters").exists());
    }

    @Test
    void getClusters_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/clusters"))
                .andExpect(status().isOk());
    }

    @Test
    void getCompliance_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/compliance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").exists());
    }
}
