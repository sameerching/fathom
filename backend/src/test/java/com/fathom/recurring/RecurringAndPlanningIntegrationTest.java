package com.fathom.recurring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
class RecurringAndPlanningIntegrationTest {
@Autowired MockMvc mockMvc; @Autowired ObjectMapper objectMapper;
String user(String email) throws Exception {return objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"U\",\"email\":\""+email+"\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();}
String account(String u) throws Exception {return objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"accountType\":\"BANK_ACCOUNT\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();}
@Test void apiCoverage() throws Exception {
String u1=user("a1@a.com"),u2=user("a2@a.com"),a1=account(u1),a2=account(u2);
String cat=objectMapper.readTree(mockMvc.perform(get("/api/categories/system")).andReturn().getResponse().getContentAsString()).get(0).get("id").asText();
String payload="{\"accountId\":\""+a1+"\",\"categoryId\":\""+cat+"\",\"name\":\"Salary\",\"amount\":1000,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"frequency\":\"MONTHLY\",\"startDate\":\"2026-01-01\"}";
String id=objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/recurring-transactions",u1).contentType(MediaType.APPLICATION_JSON).content(payload)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("id").asText();
mockMvc.perform(get("/api/users/{u}/recurring-transactions",u1)).andExpect(status().isOk()).andExpect(jsonPath("$[0].active").value(true));
mockMvc.perform(patch("/api/recurring-transactions/{id}/deactivate",id)).andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false));
mockMvc.perform(post("/api/users/{u}/recurring-transactions",u1).contentType(MediaType.APPLICATION_JSON).content(payload.replace(a1,a2))).andExpect(status().isBadRequest());
mockMvc.perform(post("/api/users/{u}/transactions",u1).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\""+a1+"\",\"transactionDate\":\"2026-05-10\",\"amount\":1500,\"direction\":\"CREDIT\",\"transactionType\":\"INCOME\",\"source\":\"MANUAL\"}"));
mockMvc.perform(post("/api/users/{u}/recurring-transactions",u1).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Q\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"INVESTMENT\",\"frequency\":\"QUARTERLY\",\"startDate\":\"2026-01-01\"}"));
mockMvc.perform(post("/api/users/{u}/recurring-transactions",u1).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Y\",\"amount\":120,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"frequency\":\"YEARLY\",\"startDate\":\"2026-05-01\"}"));
mockMvc.perform(get("/api/users/{u}/planning/monthly-summary",u1).param("month","2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedExpenses").value(120.00)).andExpect(jsonPath("$.plannedInvestments").value(0.00)).andExpect(jsonPath("$.actualIncome").value(1500.00));
mockMvc.perform(get("/api/users/{u}/planning/monthly-summary",u1).param("month","2026-04")).andExpect(status().isOk()).andExpect(jsonPath("$.plannedInvestments").value(100.00));
}
}
