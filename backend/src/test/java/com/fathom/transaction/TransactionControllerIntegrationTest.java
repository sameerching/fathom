package com.fathom.transaction;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void filterByDateRange() throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"T\",\"email\":\"t@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String accountId = objectMapper.readTree(mockMvc.perform(post("/api/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"ICICI\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/users/{userId}/transactions", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-01\",\"amount\":100.50,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(post("/api/users/{userId}/transactions", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-10\",\"amount\":200.00,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\"}"));

        mockMvc.perform(get("/api/users/{userId}/transactions", userId).param("from", "2026-05-05").param("to", "2026-05-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(200.00))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void filterByMerchantContainsIgnoreCase() throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"T2\",\"email\":\"t2@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String accountId = objectMapper.readTree(mockMvc.perform(post("/api/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"HDFC\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/users/{userId}/transactions", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-01\",\"amount\":120.00,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\",\"merchant\":\"Amazon Fresh\"}"));
        mockMvc.perform(post("/api/users/{userId}/transactions", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-02\",\"amount\":20.00,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\",\"merchant\":\"Metro\"}"));

        mockMvc.perform(get("/api/users/{userId}/transactions", userId).param("merchant", "AMAZON"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].merchant").value("Amazon Fresh"))
                .andExpect(jsonPath("$.length()").value(1));
    }
}
