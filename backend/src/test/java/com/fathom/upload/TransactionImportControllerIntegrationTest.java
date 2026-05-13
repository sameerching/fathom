package com.fathom.upload;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionImportControllerIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void importScenarios() throws Exception {
        String user1 = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"U1\",\"email\":\"u1@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String user2 = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"U2\",\"email\":\"u2@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String account = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts", user1).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A1\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String csv = "transactionDate,direction,amount,rawDescription,merchant,transactionType,categoryName,notes\n2026-05-01,DEBIT,1250.00,Swiggy order,Swiggy,EXPENSE,Food,Dinner\n2026-05-02,CREDIT,850000.00,Salary credited,Amazon,INCOME,Salary,Monthly salary\n2026-05-03,DEBIT,-1.00,Bad row,,EXPENSE,,\n";
        MockMultipartFile f = new MockMultipartFile("file","test.csv","text/csv",csv.getBytes());
        String resp = mockMvc.perform(multipart("/api/users/{u}/accounts/{a}/transaction-imports", user1, account).file(f)).andExpect(status().isOk()).andExpect(jsonPath("$.createdCount").value(2)).andExpect(jsonPath("$.failedCount").value(1)).andReturn().getResponse().getContentAsString();
        String importId = objectMapper.readTree(resp).get("importId").asText();
        mockMvc.perform(multipart("/api/users/{u}/accounts/{a}/transaction-imports", user1, account).file(f)).andExpect(status().isOk()).andExpect(jsonPath("$.skippedDuplicateCount").value(2));
        MockMultipartFile badHeader = new MockMultipartFile("file","bad.csv","text/csv","x,y\n1,2".getBytes());
        mockMvc.perform(multipart("/api/users/{u}/accounts/{a}/transaction-imports", user1, account).file(badHeader)).andExpect(status().isBadRequest());
        mockMvc.perform(multipart("/api/users/{u}/accounts/{a}/transaction-imports", user2, account).file(f)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/transaction-imports/{id}", importId)).andExpect(status().isOk()).andExpect(jsonPath("$.totalRows").value(3)).andExpect(jsonPath("$.errors[0].rowNumber").value(4));
        mockMvc.perform(get("/api/users/{u}/transaction-imports", user1)).andExpect(status().isOk());
    }

    @Test
    void unsupportedSourceReturnsBadRequest() throws Exception {
        String user = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"U4\",\"email\":\"u4@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String account = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts", user).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A4\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String csv = "transactionDate,direction,amount,rawDescription,transactionType\n2026-05-01,DEBIT,10.00,x,EXPENSE\n";
        MockMultipartFile f = new MockMultipartFile("file", "required-only.csv", "text/csv", csv.getBytes());
        mockMvc.perform(multipart("/api/users/{u}/accounts/{a}/transaction-imports", user, account).file(f).param("source", "SYSTEM"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void importWithRequiredHeadersOnly() throws Exception {
        String user = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"U3\",\"email\":\"u3@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String account = objectMapper.readTree(mockMvc.perform(post("/api/users/{u}/accounts", user).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"A3\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String csv = "transactionDate,direction,amount,rawDescription,transactionType\n2026-05-01,DEBIT,100.00,Coffee,EXPENSE\n2026-05-02,CREDIT,1000.00,Salary,INCOME\n";
        MockMultipartFile f = new MockMultipartFile("file", "required-only.csv", "text/csv", csv.getBytes());
        mockMvc.perform(multipart("/api/users/{u}/accounts/{a}/transaction-imports", user, account).file(f))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdCount").value(2))
                .andExpect(jsonPath("$.failedCount").value(0));
    }

}
