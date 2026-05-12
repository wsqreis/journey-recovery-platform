package com.travelcx.recovery.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;

class CaseQueryServiceTests {
    @Test
    void delegatesDraftGenerationToTheAdapter() {
        CaseCacheService caseCacheService = org.mockito.Mockito.mock(CaseCacheService.class);
        DraftMessageService draftMessageService = org.mockito.Mockito.mock(DraftMessageService.class);
        CaseQueryService caseQueryService = new CaseQueryService(caseCacheService, draftMessageService);
        CachedCaseSnapshot snapshot = org.mockito.Mockito.mock(CachedCaseSnapshot.class);
        given(caseCacheService.getSnapshot("case-300")).willReturn(snapshot);
        given(draftMessageService.draftMessage(snapshot)).willReturn("Drafted customer message");

        var draft = caseQueryService.previewDraft("case-300");

        assertThat(draft.caseId()).isEqualTo("case-300");
        assertThat(draft.draftMessage()).isEqualTo("Drafted customer message");
    }
}
