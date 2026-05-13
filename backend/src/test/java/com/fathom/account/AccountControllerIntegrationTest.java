package com.fathom.account;

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
class AccountControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void createAndList() throws Exception {
        String userResponse = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"U\",\"email\":\"u@a.com\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode userNode = objectMapper.readTree(userResponse);
        String userId = userNode.get("id").asText();

        mockMvc.perform(post("/api/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"HDFC\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{userId}/accounts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("HDFC"));
    }
}
