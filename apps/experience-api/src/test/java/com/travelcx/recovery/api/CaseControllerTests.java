package com.travelcx.recovery.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travelcx.recovery.contracts.CustomerProfileResponse;
import com.travelcx.recovery.contracts.DisruptionCaseResponse;
import com.travelcx.recovery.contracts.DraftMessageResponse;
import com.travelcx.recovery.contracts.RecommendationResponse;
import com.travelcx.recovery.contracts.TripSegmentResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CaseController.class)
@AutoConfigureMockMvc
class CaseControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseQueryService caseQueryService;

    @Test
    void returnsSeededCaseDetails() throws Exception {
        given(caseQueryService.getCase("case-001")).willReturn(new DisruptionCaseResponse(
                "case-001",
                "BR1234",
                "FLIGHT_DELAY",
                OffsetDateTime.of(2026, 5, 12, 10, 15, 0, 0, ZoneOffset.UTC),
                2,
                95,
                true,
                false,
                new CustomerProfileResponse("cust-1", "Taylor Rivera", "PRIME", false, false),
                List.of(new TripSegmentResponse(
                        "MAD",
                        "BCN",
                        "IB",
                        "IB411",
                        OffsetDateTime.of(2026, 5, 12, 11, 0, 0, 0, ZoneOffset.UTC),
                        OffsetDateTime.of(2026, 5, 12, 12, 20, 0, 0, ZoneOffset.UTC)))));

        mockMvc.perform(get("/api/cases/case-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value("case-001"))
                .andExpect(jsonPath("$.bookingReference").value("BR1234"))
                .andExpect(jsonPath("$.customer.loyaltyTier").value("PRIME"));
    }

    @Test
    void returnsRecommendationForSeededCase() throws Exception {
        given(caseQueryService.getRecommendation("case-002"))
                .willReturn(new RecommendationResponse(
                        "REBOOK",
                        110,
                        "Offer an alternative itinerary before customer effort increases.",
                        "A missed connection risk makes itinerary recovery time-sensitive.",
                        List.of("A missed connection risk makes itinerary recovery time-sensitive.")));

        mockMvc.perform(get("/api/cases/case-002/recommendation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("REBOOK"))
                .andExpect(jsonPath("$.reasons[0]").exists());
    }

    @Test
    void returnsDraftMessagePreview() throws Exception {
        given(caseQueryService.previewDraft("case-001"))
                .willReturn(new DraftMessageResponse(
                        "case-001",
                        "Hello Taylor Rivera, we detected a flight delay on booking BR1234."));

        mockMvc.perform(get("/api/cases/case-001/draft-message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value("case-001"))
                .andExpect(jsonPath("$.draftMessage").value(org.hamcrest.Matchers.containsString("Taylor Rivera")));
    }

    @Test
    void returnsNotFoundForUnknownCase() throws Exception {
        given(caseQueryService.getCase("missing-case")).willThrow(new NoSuchElementException("Case not found: missing-case"));

        mockMvc.perform(get("/api/cases/missing-case"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Case not found: missing-case"));
    }
}
