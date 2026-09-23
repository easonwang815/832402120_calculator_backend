package com.calculator.controller;

import com.calculator.model.CalculationHistory;
import com.calculator.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:api-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
@Transactional
class CalculatorApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HistoryRepository historyRepository;

    @Test
    void calculatesAndStoresHistory() throws Exception {
        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\":\"(1+2)*3\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value("9"));

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].expression").value("(1+2)*3"))
                .andExpect(jsonPath("$.data.records[0].result").value("9"));
    }

    @Test
    void rejectsDotOnlyAsInvalidExpression() throws Exception {
        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\":\".\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid expression"));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsInvalidHistoryId() throws Exception {
        mockMvc.perform(delete("/api/history/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("{\"expression\":\"1+2\"}"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("Unsupported media type"));
    }

    @Test
    void returnsNotFoundForUnknownApiPath() throws Exception {
        mockMvc.perform(get("/api/not-there"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void storesLongResultWithoutDatabaseOverflow() throws Exception {
        String number = "999999999999999999999999999999999999999999999999999";

        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\":\"" + number + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(number));
    }

    @Test
    void deletesExistingHistory() throws Exception {
        CalculationHistory history = historyRepository.save(new CalculationHistory("1+2", "3"));

        mockMvc.perform(delete("/api/history/{id}", history.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records").isEmpty());
    }

    @Test
    void calculatesScientificExpression() throws Exception {
        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\":\"sqrt(9)+2^3+sin(30)\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("11.5"));
    }

    @Test
    void searchesAndPaginatesHistory() throws Exception {
        historyRepository.save(new CalculationHistory("1+2", "3"));
        historyRepository.save(new CalculationHistory("10+20", "30"));
        historyRepository.save(new CalculationHistory("5*5", "25"));

        mockMvc.perform(get("/api/history")
                        .param("keyword", "+")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    void rejectsInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/history").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid pagination parameters"));
    }

    @Test
    void convertsBetweenNumberBases() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"255\",\"fromBase\":10,\"toBase\":16}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").value("FF"));

        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"101010\",\"fromBase\":2,\"toBase\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result").value("42"));
    }

    @Test
    void rejectsInvalidDigitForBase() throws Exception {
        mockMvc.perform(post("/api/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"102\",\"fromBase\":2,\"toBase\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid number for source base"));
    }
}
