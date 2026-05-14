package com.fathom.rule;

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
class CategoryRuleIntegrationTest { @Autowired MockMvc mockMvc; @Autowired ObjectMapper om;
 @Test void createListUpdateDeactivate() throws Exception {
 String u=om.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"R\",\"email\":\"r1@a.com\",\"status\":\"ACTIVE\"}")).andReturn().getResponse().getContentAsString()).get("id").asText();
 String c=om.readTree(mockMvc.perform(post("/api/users/{u}/categories",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"FoodX\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}")).andReturn().getResponse().getContentAsString()).get("id").asText();
 String rule=om.readTree(mockMvc.perform(post("/api/users/{u}/category-rules",u).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Swiggy\",\"ruleField\":\"MERCHANT\",\"matchOperator\":\"CONTAINS\",\"matchValue\":\"swiggy\",\"categoryId\":\""+c+"\"}")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("id").asText();
 mockMvc.perform(get("/api/users/{u}/category-rules",u)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(rule));
 mockMvc.perform(patch("/api/category-rules/{id}",rule).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Swiggy2\",\"priority\":5,\"ruleField\":\"MERCHANT\",\"matchOperator\":\"CONTAINS\",\"matchValue\":\"swiggy\",\"categoryId\":\""+c+"\",\"active\":true}")).andExpect(status().isOk());
 mockMvc.perform(patch("/api/category-rules/{id}/deactivate",rule)).andExpect(status().isOk());
 }
}
