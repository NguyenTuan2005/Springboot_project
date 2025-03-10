package com.example.surveyservice.controller;

import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.service.CROService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CROController.class)
@WithMockUser(username = "test")
public class CROControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CROService croService;

    @Test
    public void testGetAllCROTests() throws Exception {
        CROTest test1 = new CROTest();
        test1.setId(1L);
        test1.setName("Test 1");
        CROTest test2 = new CROTest();
        test2.setId(2L);
        test2.setName("Test 2");
        when(croService.getAllTests()).thenReturn(Arrays.asList(test1, test2));

        mockMvc.perform(get("/api/v1/cro/tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Test 1"))
                .andExpect(jsonPath("$[1].name").value("Test 2"));
    }

    @Test
    public void testGetCROTest() throws Exception {
        CROTest test = new CROTest();
        test.setId(1L);
        test.setName("A/B Test");
        test.setTargetUrl("http://example.com");
        test.setVariants(Arrays.asList("A", "B"));
        when(croService.getTestById(1L)).thenReturn(Optional.of(test));

        mockMvc.perform(get("/api/v1/cro/tests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A/B Test"))
                .andExpect(jsonPath("$.targetUrl").value("http://example.com"))
                .andExpect(jsonPath("$.variants[0]").value("A"));
    }

    @Test
    public void testGetCROTestByIdNotFound() throws Exception {
        when(croService.getTestById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tests/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCROTest() throws Exception {
        CROTest test = new CROTest();
        test.setId(1L);
        test.setName("New Test");
        test.setTargetUrl("http://new.com");
        test.setVariants(Arrays.asList("X", "Y"));
        when(croService.createTest(any(CROTest.class))).thenReturn(test);

        mockMvc.perform(post("/api/v1/cro/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Test\", \"targetUrl\": \"http://new.com\", \"variants\": [\"X\", \"Y\"]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test"))
                .andExpect(jsonPath("$.variants[1]").value("Y"));
    }

    @Test
    public void testUpdateCROTest() throws Exception {
        CROTest test = new CROTest();
        test.setId(1L);
        test.setName("Updated Test");
        test.setTargetUrl("http://updated.com");
        test.setVariants(Collections.singletonList("Z"));
        when(croService.updateTest(eq(1L), any(CROTest.class))).thenReturn(Optional.of(test));

        mockMvc.perform(put("/api/v1/cro/tests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Test\", \"targetUrl\": \"http://updated.com\", \"variants\": [\"Z\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Test"))
                .andExpect(jsonPath("$.variants[0]").value("Z"));
    }

    @Test
    public void testDeleteCROTest() throws Exception {
        when(croService.deleteTest(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/cro/tests/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllCROResults() throws Exception {
        CROTest test = new CROTest();
        test.setId(1L);
        CROResult result1 = new CROResult();
        result1.setId(1L);
        result1.setTest(test);
        result1.setVariant("A");
        CROResult result2 = new CROResult();
        result2.setId(2L);
        result2.setTest(test);
        result2.setVariant("B");
        when(croService.getAllResults()).thenReturn(Arrays.asList(result1, result2));

        mockMvc.perform(get("/api/v1/cro/results/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].variant").value("A"))
                .andExpect(jsonPath("$[1].variant").value("B"));
    }

    @Test
    public void testGetCROResultById() throws Exception {
        CROTest test = new CROTest();
        test.setId(1L);
        CROResult result = new CROResult();
        result.setId(1L);
        result.setTest(test);
        result.setVariant("A");
        result.setImpressions(100);
        result.setConversions(10);
        when(croService.getResultById(1L)).thenReturn(Optional.of(result));

        mockMvc.perform(get("/api/v1/cro/results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.variant").value("A"))
                .andExpect(jsonPath("$.impressions").value(100))
                .andExpect(jsonPath("$.conversions").value(10));
    }
}
