package com.example.surveyservice.controller;

import com.example.surveyservice.model.Survey;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import com.example.surveyservice.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyControllerTest {

    @Mock
    private SurveyService surveyService;

    @InjectMocks
    private SurveyController surveyController;

    private MockMvc mockMvc;
    private Survey survey;
    private SurveyResponse surveyResponse;
    private CreateSurveyResponseRequest request;
    private Map<String, Object> answers;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(surveyController).build();

        // Setup test data
        survey = new Survey();
        survey.setId(1L);
        survey.setTitle("Test Survey");
        survey.setTargetIndustry("Technology");

        answers = new HashMap<>();
        answers.put("question1", "answer1");
        answers.put("question2", "answer2");

        surveyResponse = new SurveyResponse();
        surveyResponse.setId(1L);
        surveyResponse.setSurvey(survey);
        surveyResponse.setCompanySize("Medium");
        surveyResponse.setAnswers(answers);

        request = new CreateSurveyResponseRequest();
        request.setCompanySize("Medium");
        request.setAnswers(answers);
    }

    @Test
    void testCreateSurvey() {
        // Given
        when(surveyService.createSurvey(any(Survey.class))).thenReturn(survey);

        // When
        ResponseEntity<Survey> response = surveyController.createSurvey(survey);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(surveyService, times(1)).createSurvey(any(Survey.class));
    }

    @Test
    void testGetSurveyById_Found() {
        // Given
        when(surveyService.getSurveyById(anyLong())).thenReturn(Optional.of(survey));

        // When
        ResponseEntity<Survey> response = surveyController.getSurveyById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(surveyService, times(1)).getSurveyById(1L);
    }

    @Test
    void testGetSurveyById_NotFound() {
        // Given
        when(surveyService.getSurveyById(anyLong())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Survey> response = surveyController.getSurveyById(999L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(surveyService, times(1)).getSurveyById(999L);
    }

    @Test
    void testGetAllSurveys() {
        // Given
        List<Survey> surveys = Arrays.asList(survey);
        when(surveyService.getAllSurveys()).thenReturn(surveys);

        // When
        ResponseEntity<List<Survey>> response = surveyController.getAllSurveys();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(surveyService, times(1)).getAllSurveys();
    }

    @Test
    void testGetSurveysByTargetIndustry() {
        // Given
        List<Survey> surveys = Arrays.asList(survey);
        when(surveyService.getSurveysByTargetIndustry(anyString())).thenReturn(surveys);

        // When
        ResponseEntity<List<Survey>> response = surveyController.getSurveysByTargetIndustry("Technology");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(surveyService, times(1)).getSurveysByTargetIndustry("Technology");
    }

    @Test
    void testUpdateSurvey_Found() {
        // Given
        when(surveyService.updateSurvey(anyLong(), any(Survey.class))).thenReturn(Optional.of(survey));

        // When
        ResponseEntity<Survey> response = surveyController.updateSurvey(1L, survey);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(surveyService, times(1)).updateSurvey(1L, survey);
    }

    @Test
    void testUpdateSurvey_NotFound() {
        // Given
        when(surveyService.updateSurvey(anyLong(), any(Survey.class))).thenReturn(Optional.empty());

        // When
        ResponseEntity<Survey> response = surveyController.updateSurvey(999L, survey);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(surveyService, times(1)).updateSurvey(999L, survey);
    }

    @Test
    void testDeleteSurvey_Found() {
        // Given
        when(surveyService.deleteSurvey(anyLong())).thenReturn(true);

        // When
        ResponseEntity<Void> response = surveyController.deleteSurvey(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(surveyService, times(1)).deleteSurvey(1L);
    }

    @Test
    void testDeleteSurvey_NotFound() {
        // Given
        when(surveyService.deleteSurvey(anyLong())).thenReturn(false);

        // When
        ResponseEntity<Void> response = surveyController.deleteSurvey(999L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(surveyService, times(1)).deleteSurvey(999L);
    }

    @Test
    void testCreateSurveyResponse_SurveyNotFound() {
        // Given

        when(surveyService.createSurveyResponse(anyLong(), any(CreateSurveyResponseRequest.class)))
                .thenThrow(new IllegalArgumentException("Survey not found"));

        // When
        ResponseEntity<SurveyResponse> response = surveyController.createSurveyResponse(999L, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(surveyService, times(1)).createSurveyResponse(999L, request);
    }

    @Test
    void testGetSurveyResponses() {
        // Given
        List<SurveyResponse> responses = Arrays.asList(surveyResponse);
        when(surveyService.getSurveyResponses(anyLong())).thenReturn(responses);

        // When
        ResponseEntity<List<SurveyResponse>> response = surveyController.getSurveyResponses(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(surveyService, times(1)).getSurveyResponses(1L);
    }

    @Test
    void testCreateSurveyResponse() {
        // Given

        when(surveyService.createSurveyResponse(anyLong(), any(CreateSurveyResponseRequest.class))).thenReturn(surveyResponse);

        // When
        ResponseEntity<SurveyResponse> response = surveyController.createSurveyResponse(1L, request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(surveyService, times(1)).createSurveyResponse(1L, request);
    }

    @Test
    void testGetSurveyResponsesPaginated() {
        // Given
        Page<SurveyResponse> page = new PageImpl<>(Arrays.asList(surveyResponse));
        when(surveyService.getSurveyResponsesPaginated(anyLong(), any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<SurveyResponse>> response = surveyController.getSurveyResponsesPaginated(
                1L, 0, 10, "id", "asc");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(surveyService, times(1)).getSurveyResponsesPaginated(eq(1L), any(Pageable.class));
    }
}
