package com.fathom.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc;
@SpringBootTest @AutoConfigureMockMvc class UserControllerIntegrationTest { @Autowired MockMvc mockMvc;
 @Test void createAndGetUser() throws Exception { String body="{\"name\":\"A\",\"email\":\"a@a.com\",\"status\":\"ACTIVE\"}"; var res=mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andReturn(); String id=res.getResponse().getContentAsString().split("\"id\":\"")[1].split("\"")[0]; mockMvc.perform(get("/api/users/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("a@a.com")); }}
