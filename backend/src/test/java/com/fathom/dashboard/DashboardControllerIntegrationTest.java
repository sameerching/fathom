package com.fathom.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void monthlySummaryCalculation() throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"DU\",\"email\":\"du@a.com\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        String accountId = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"accountType\":\"BANK_ACCOUNT\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/api/users/{u}/transactions", userId).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-01\",\"amount\":1000,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(post("/api/users/{u}/transactions", userId).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-02\",\"amount\":200,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(post("/api/users/{u}/transactions", userId).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-03\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"LIABILITY_PAYMENT\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(get("/api/users/{u}/dashboard/monthly-summary", userId).param("month", "2026-05"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.netCashFlow").value(700.00)).andExpect(jsonPath("$.savingsRate").value(70.00));
    }

    @Test
    void categoryBreakdownCalculation() throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"DU2\",\"email\":\"du2@a.com\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        String accountId = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"accountType\":\"BANK_ACCOUNT\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        String categoryId = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/categories", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Food\",\"categoryType\":\"EXPENSE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/api/users/{u}/transactions", userId).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"" + accountId + "\",\"categoryId\":\"" + categoryId + "\",\"transactionDate\":\"2026-05-01\",\"amount\":300,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(post("/api/users/{u}/transactions", userId).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-02\",\"amount\":150,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(get("/api/users/{u}/dashboard/category-breakdown", userId).param("from", "2026-05-01").param("to", "2026-05-31"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].categoryName").value("Food")).andExpect(jsonPath("$[1].categoryName").value("Uncategorized"));
    }

    @Test
    void netWorthSummaryCalculation() throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"DU3\",\"email\":\"du3@a.com\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/api/users/{u}/investments", userId).contentType(MediaType.APPLICATION_JSON).content("{\"assetType\":\"MUTUAL_FUND\",\"name\":\"MF1\",\"investedAmount\":1000,\"currentValue\":1200,\"active\":true}"));
        mockMvc.perform(post("/api/users/{u}/liabilities", userId).contentType(MediaType.APPLICATION_JSON).content("{\"liabilityType\":\"HOME_LOAN\",\"name\":\"L1\",\"outstandingAmount\":500,\"active\":true}"));
        mockMvc.perform(get("/api/users/{u}/dashboard/net-worth", userId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.netWorth").value(700.00));
    }
}
