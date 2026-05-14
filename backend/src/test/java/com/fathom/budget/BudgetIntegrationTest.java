package com.fathom.budget;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest @AutoConfigureMockMvc
class BudgetIntegrationTest {
 @Autowired MockMvc mockMvc; @Autowired ObjectMapper objectMapper;
 private String user(String n,String e) throws Exception{return objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\""+n+"\",\"email\":\""+e+"\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();}
 private String account(String u) throws Exception{return objectMapper.readTree(mockMvc.perform(post("/api/users/{userId}/accounts",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A\",\"accountType\":\"BANK_ACCOUNT\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();}
 @Test void createListDeactivate() throws Exception {String u=user("U","u@x.com"); String b=objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/budgets",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Food\",\"month\":\"2026-05\",\"amount\":1000,\"active\":true}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("id").asText(); mockMvc.perform(get("/api/users/{u}/budgets",u).param("month","2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Food")); mockMvc.perform(patch("/api/budgets/{id}/deactivate",b)).andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false)); }
 @Test void categoryOwnershipValidation() throws Exception {String u1=user("A","a@x.com"); String u2=user("B","b@x.com"); String c2=objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/categories",u2).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Private\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}")).andReturn().getResponse().getContentAsString()).get("id").asText(); mockMvc.perform(post("/api/users/{u}/budgets",u1).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Food\",\"month\":\"2026-05\",\"amount\":1000,\"categoryId\":\""+c2+"\"}")).andExpect(status().isBadRequest()); }
 @Test void summaryUnderOverAndGeneral() throws Exception {String u=user("C","c@x.com"); String a=account(u); String c=objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/categories",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Food\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}")).andReturn().getResponse().getContentAsString()).get("id").asText(); mockMvc.perform(post("/api/users/{u}/transactions",u).contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\""+a+"\",\"transactionDate\":\"2026-05-02\",\"amount\":600,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\",\"categoryId\":\""+c+"\"}")); mockMvc.perform(post("/api/users/{u}/budgets",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Food Under\",\"month\":\"2026-05\",\"amount\":1000,\"categoryId\":\""+c+"\"}")); mockMvc.perform(post("/api/users/{u}/budgets",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Food Over\",\"month\":\"2026-05\",\"amount\":500,\"categoryId\":\""+c+"\"}")); mockMvc.perform(post("/api/users/{u}/budgets",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"General\",\"month\":\"2026-05\",\"amount\":700}")); mockMvc.perform(get("/api/users/{u}/budget-summary",u).param("month","2026-05")).andExpect(status().isOk()).andExpect(jsonPath("$[0].status").exists()).andExpect(jsonPath("$[0].actualAmount").exists()); }
}
