package com.fathom.rule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
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
class CategoryRuleIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void applyRulesToExistingUncategorizedTransactions() throws Exception {
        TestIds ids = setupUserAccountAndCategory("rapply@a.com", "FoodX");
        createTransaction(ids.userId, ids.accountId, "Swiggy Instamart", null);
        createTransaction(ids.userId, ids.accountId, "No Match", null);
        createRule(ids.userId, ids.categoryId, 10, "swiggy");

        mockMvc.perform(post("/api/users/{userId}/category-rules/apply", ids.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedCount").value(1))
                .andExpect(jsonPath("$.updatedCount").value(1))
                .andExpect(jsonPath("$.skippedCount").value(0));

        mockMvc.perform(get("/api/users/{userId}/transactions", ids.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(ids.categoryId));
    }

    @Test
    void firstMatchingRuleWinsByPriority() throws Exception {
        TestIds ids = setupUserAccountAndCategory("rpriority@a.com", "FoodP");
        String otherCategory = createCategory(ids.userId, "GroceriesP");
        createTransaction(ids.userId, ids.accountId, "Swiggy Instamart", null);
        createRule(ids.userId, otherCategory, 20, "swiggy");
        createRule(ids.userId, ids.categoryId, 5, "swiggy");

        mockMvc.perform(post("/api/users/{userId}/category-rules/apply", ids.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedCount").value(1));

        mockMvc.perform(get("/api/users/{userId}/transactions", ids.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(ids.categoryId));
    }

    @Test
    void csvImportAutoCategorizesAndNonMatchingRemainUncategorized() throws Exception {
        TestIds ids = setupUserAccountAndCategory("rcsv@a.com", "FoodCsv");
        createRule(ids.userId, ids.categoryId, 10, "swiggy");

        String csv = "transactionDate,direction,amount,rawDescription,merchant,transactionType\n" +
                "2026-05-01,DEBIT,120,UPI/SWIGGY/ORDER,Swiggy Instamart,EXPENSE\n" +
                "2026-05-02,DEBIT,200,UPI/OTHER,Unknown,EXPENSE\n";

        mockMvc.perform(multipart("/api/users/{userId}/accounts/{accountId}/transaction-imports", ids.userId, ids.accountId)
                        .file(new MockMultipartFile("file", "tx.csv", "text/csv", csv.getBytes()))
                        .param("source", "MANUAL"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{userId}/transactions", ids.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].categoryId").value(ids.categoryId))
                .andExpect(jsonPath("$[0].categoryId").isEmpty());
    }

    @Test
    void manualTransactionCreationAutoCategorizesMatchingRows() throws Exception {
        TestIds ids = setupUserAccountAndCategory("rmanual@a.com", "FoodManual");
        createRule(ids.userId, ids.categoryId, 10, "swiggy");

        mockMvc.perform(post("/api/users/{userId}/transactions", ids.userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + ids.accountId + "\",\"transactionDate\":\"2026-05-10\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\",\"merchant\":\"Swiggy Instamart\",\"rawDescription\":\"Food order\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(ids.categoryId));
    }

    @Test
    void csvCategoryNameWinsOverRules() throws Exception {
        TestIds ids = setupUserAccountAndCategory("rcatwins@a.com", "FoodWin");
        String groceriesId = createCategory(ids.userId, "GroceriesWin");
        createRule(ids.userId, groceriesId, 10, "swiggy");

        String csv = "transactionDate,direction,amount,rawDescription,merchant,transactionType,categoryName\n" +
                "2026-05-01,DEBIT,120,UPI/SWIGGY/ORDER,Swiggy Instamart,EXPENSE,FoodWin\n";

        mockMvc.perform(multipart("/api/users/{userId}/accounts/{accountId}/transaction-imports", ids.userId, ids.accountId)
                        .file(new MockMultipartFile("file", "tx2.csv", "text/csv", csv.getBytes()))
                        .param("source", "MANUAL"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{userId}/transactions", ids.userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(ids.categoryId));
    }

    @Test
    void ruleCannotUseAnotherUsersCategory() throws Exception {
        TestIds a = setupUserAccountAndCategory("ra@a.com", "FoodA");
        TestIds b = setupUserAccountAndCategory("rb@a.com", "FoodB");

        mockMvc.perform(post("/api/users/{userId}/category-rules", a.userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bad\",\"ruleField\":\"MERCHANT\",\"matchOperator\":\"CONTAINS\",\"matchValue\":\"x\",\"categoryId\":\"" + b.categoryId + "\"}"))
                .andExpect(status().isBadRequest());
    }

    private TestIds setupUserAccountAndCategory(String email, String categoryName) throws Exception {
        String userId = objectMapper.readTree(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"U\",\"email\":\"" + email + "\",\"status\":\"ACTIVE\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String accountId = objectMapper.readTree(mockMvc.perform(post("/api/users/{userId}/accounts", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"accountType\":\"BANK_ACCOUNT\"}"))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        String categoryId = createCategory(userId, categoryName);
        return new TestIds(userId, accountId, categoryId);
    }

    private String createCategory(String userId, String name) throws Exception {
        JsonNode node = objectMapper.readTree(mockMvc.perform(post("/api/users/{userId}/categories", userId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"categoryType\":\"EXPENSE\",\"parentCategoryId\":null}"))
                .andReturn().getResponse().getContentAsString());
        return node.get("id").asText();
    }

    private void createRule(String userId, String categoryId, int priority, String matchValue) throws Exception {
        mockMvc.perform(post("/api/users/{userId}/category-rules", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rule\",\"priority\":" + priority + ",\"ruleField\":\"MERCHANT\",\"matchOperator\":\"CONTAINS\",\"matchValue\":\"" + matchValue + "\",\"categoryId\":\"" + categoryId + "\",\"transactionType\":\"EXPENSE\",\"direction\":\"DEBIT\",\"active\":true}"))
                .andExpect(status().isOk());
    }

    private void createTransaction(String userId, String accountId, String merchant, String categoryIdOrNull) throws Exception {
        String categoryPart = categoryIdOrNull == null ? "\"categoryId\":null," : "\"categoryId\":\"" + categoryIdOrNull + "\",";
        mockMvc.perform(post("/api/users/{userId}/transactions", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" + categoryPart + "\"accountId\":\"" + accountId + "\",\"transactionDate\":\"2026-05-01\",\"amount\":100,\"direction\":\"DEBIT\",\"transactionType\":\"EXPENSE\",\"source\":\"MANUAL\",\"merchant\":\"" + merchant + "\",\"rawDescription\":\"desc\"}"))
                .andExpect(status().isOk());
    }

    private record TestIds(String userId, String accountId, String categoryId) {}
}
