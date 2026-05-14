package com.fathom.recurring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
class RecurringAndPlanningIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    String user(String email) throws Exception { return objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"U\",\"email\":\"" + email + "\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText(); }
    String account(String userId) throws Exception { return objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts", userId).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"accountType\":\"BANK_ACCOUNT\"}")).andReturn().getResponse().getContentAsString()).get("id").asText(); }
    String systemCategory() throws Exception { return objectMapper.readTree(mockMvc.perform(get("/api/categories/system")).andReturn().getResponse().getContentAsString()).get(0).get("id").asText(); }

    @Test void createRecurringTransaction() throws Exception {
        String u = user("crt@a.com"); String a = account(u); String c = systemCategory();
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\""+a+"\",\"categoryId\":\""+c+"\",\"name\":\"EMI\",\"amount\":55000,\"direction\":\"DEBIT\",\"transactionType\":\"LIABILITY_PAYMENT\",\"frequency\":\"MONTHLY\",\"dayOfMonth\":5,\"startDate\":\"2026-05-01\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("EMI"));
    }

    @Test void listRecurringTransactions() throws Exception {
        String u = user("list@a.com");
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}"));
        mockMvc.perform(get("/api/users/{u}/recurring-transactions", u)).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test void deactivateRecurringTransaction() throws Exception {
        String u = user("deact@a.com");
        String id = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(patch("/api/recurring-transactions/{id}/deactivate", id)).andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false));
    }

    @Test void accountOwnershipValidation() throws Exception {
        String u1 = user("owner1@a.com"); String u2 = user("owner2@a.com"); String a2 = account(u2);
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u1).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\""+a2+"\",\"name\":\"Bad\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test void categoryOwnershipValidation() throws Exception {
        String u1 = user("cat1@a.com"); String u2 = user("cat2@a.com");
        String userCategory = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/categories", u1).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Mine\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}")).andReturn().getResponse().getContentAsString()).get("id").asText();
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u2).contentType(MediaType.APPLICATION_JSON).content("{\"categoryId\":\""+userCategory+"\",\"name\":\"Bad\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test void monthlyPlanningSummaryWithPlannedOnly() throws Exception {
        String u = user("planonly@a.com");
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Salary\",\"amount\":1000,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}"));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedIncome").value(1000.00)).andExpect(jsonPath("$.actualIncome").value(0.00));
    }

    @Test void monthlyPlanningSummaryWithActualOnly() throws Exception {
        String u = user("actualonly@a.com"); String a = account(u);
        mockMvc.perform(post("/api/users/{u}/transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\""+a+"\",\"transactionDate\":\"2026-05-10\",\"amount\":1500,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedIncome").value(0.00)).andExpect(jsonPath("$.actualIncome").value(1500.00));
    }

    @Test void monthlyPlanningSummaryWithVariance() throws Exception {
        String u = user("variance@a.com"); String a = account(u);
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Salary\",\"amount\":1000,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}"));
        mockMvc.perform(post("/api/users/{u}/transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\""+a+"\",\"transactionDate\":\"2026-05-10\",\"amount\":1500,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"source\":\"MANUAL\"}"));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$.incomeVariance").value(500.00));
    }

    @Test void quarterlyRecurringApplicability() throws Exception {
        String u = user("quarter@a.com");
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"SIP\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"INVESTMENT\",\"frequency\":\"QUARTERLY\",\"startDate\":\"2026-01-01\"}"));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-04")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedInvestments").value(100.00));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedInvestments").value(0.00));
    }

    @Test void yearlyRecurringApplicability() throws Exception {
        String u = user("year@a.com");
        mockMvc.perform(post("/api/users/{u}/recurring-transactions", u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Insurance\",\"amount\":120,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"frequency\":\"YEARLY\",\"startDate\":\"2026-05-01\"}"));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedExpenses").value(120.00));
        mockMvc.perform(get("/api/users/{u}/planning/monthly-summary", u).param("month", "2026-06")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedExpenses").value(0.00));
    }
}
