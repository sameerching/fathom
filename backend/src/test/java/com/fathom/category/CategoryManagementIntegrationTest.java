package com.fathom.category;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class CategoryManagementIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void createAndDuplicateCategory() throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"U\",\"email\":\"u1@a.com\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/users/{userId}/categories", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Eating Out\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eating Out"));

        mockMvc.perform(post("/api/users/{userId}/categories", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"eating out\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}"))
                .andExpect(status().isConflict());
    }

    @Test
    void cannotUpdateSystemCategory() throws Exception {
        mockMvc.perform(patch("/api/categories/{categoryId}", "00000000-0000-0000-0000-000000000002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Food & Dining\",\"categoryType\":\"EXPENSE\",\"active\":true}"))
                .andExpect(status().isBadRequest());
    }
}
