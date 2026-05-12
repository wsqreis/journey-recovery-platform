package com.travelcx.recovery.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CaseControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsSeededCaseDetails() throws Exception {
        mockMvc.perform(get("/api/cases/case-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value("case-001"))
                .andExpect(jsonPath("$.bookingReference").value("BR1234"))
                .andExpect(jsonPath("$.customer.loyaltyTier").value("PRIME"));
    }

    @Test
    void returnsRecommendationForSeededCase() throws Exception {
        mockMvc.perform(get("/api/cases/case-002/recommendation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("REBOOK"))
                .andExpect(jsonPath("$.reasons[0]").exists());
    }

    @Test
    void returnsDraftMessagePreview() throws Exception {
        mockMvc.perform(get("/api/cases/case-001/draft-message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value("case-001"))
                .andExpect(jsonPath("$.draftMessage").value(org.hamcrest.Matchers.containsString("Taylor Rivera")));
    }

    @Test
    void returnsNotFoundForUnknownCase() throws Exception {
        mockMvc.perform(get("/api/cases/missing-case"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Case not found: missing-case"));
    }
}
