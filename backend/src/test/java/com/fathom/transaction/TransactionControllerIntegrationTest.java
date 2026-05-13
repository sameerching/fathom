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
    void createAndList() throws Exception {
        String userResponse = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"T\",\"email\":\"t@a.com\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String userId = objectMapper.readTree(userResponse).get("id").asText();

        String accountResponse = mockMvc.perform(post("/api/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ICICI\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode accountNode = objectMapper.readTree(accountResponse);
        String accountId = accountNode.get("id").asText();

        mockMvc.perform(post("/api/users/{userId}/transactions", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-01\",\"amount\":100.50,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{userId}/transactions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.50));
    }
}
