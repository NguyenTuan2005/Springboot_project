package com.example.surveyservice.service;

import com.example.surveyservice.config.RabbitMQConfig;
import com.example.surveyservice.model.Survey;
import com.example.surveyservice.model.SurveyResponse;
import com.example.surveyservice.repository.SurveyRepository;
import com.example.surveyservice.repository.SurveyResponseRepository;
import com.example.surveyservice.request.CreateSurveyResponseRequest;
import com.example.surveyservice.service.impl.SurveyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private SurveyResponseRepository surveyResponseRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;
    private SurveyService surveyService;

    private Survey survey;
    private SurveyResponse surveyResponse;
    private CreateSurveyResponseRequest request;
    private Map<String, Object> answers;

    @BeforeEach
    void setUp() {
        surveyService = new SurveyServiceImpl(surveyResponseRepository, surveyRepository, rabbitTemplate);

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

        request = CreateSurveyResponseRequest.builder()
                .companySize("Medium")
                .answers(answers)
                .build();
    }

    @Test
    void testCreateSurvey() {
        // Given
        when(surveyRepository.save(any(Survey.class))).thenReturn(survey);

        // When
        Survey result = surveyService.createSurvey(survey);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Survey", result.getTitle());
        assertEquals("Technology", result.getTargetIndustry());
        verify(surveyRepository, times(1)).save(any(Survey.class));
    }

    @Test
    void testGetSurveyById_Found() {
        // Given
        when(surveyRepository.findById(anyLong())).thenReturn(Optional.of(survey));

        // When
        Optional<Survey> result = surveyService.getSurveyById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(surveyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSurveyById_NotFound() {
        // Given
        when(surveyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Survey> result = surveyService.getSurveyById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(surveyRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllSurveys() {
        // Given
        List<Survey> surveys = Arrays.asList(survey);
        when(surveyRepository.findAll()).thenReturn(surveys);

        // When
        List<Survey> result = surveyService.getAllSurveys();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(surveyRepository, times(1)).findAll();
    }

    @Test
    void testGetSurveysByTargetIndustry() {
        // Given
        List<Survey> surveys = Arrays.asList(survey);
        when(surveyRepository.findByTargetIndustry(anyString())).thenReturn(surveys);

        // When
        List<Survey> result = surveyService.getSurveysByTargetIndustry("Technology");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Technology", result.get(0).getTargetIndustry());
        verify(surveyRepository, times(1)).findByTargetIndustry("Technology");
    }

    @Test
    void testUpdateSurvey_Found() {
        // Given
        Survey updatedSurvey = new Survey();
        updatedSurvey.setId(1L);
        updatedSurvey.setTitle("Updated Survey");
        updatedSurvey.setTargetIndustry("Healthcare");

        when(surveyRepository.findById(anyLong())).thenReturn(Optional.of(survey));
        when(surveyRepository.save(any(Survey.class))).thenReturn(updatedSurvey);

        // When
        Optional<Survey> result = surveyService.updateSurvey(1L, updatedSurvey);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Updated Survey", result.get().getTitle());
        assertEquals("Healthcare", result.get().getTargetIndustry());
        verify(surveyRepository, times(1)).findById(1L);
        verify(surveyRepository, times(1)).save(any(Survey.class));
    }

    @Test
    void testUpdateSurvey_NotFound() {
        // Given
        Survey updatedSurvey = new Survey();
        updatedSurvey.setTitle("Updated Survey");
        updatedSurvey.setTargetIndustry("Healthcare");

        when(surveyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Survey> result = surveyService.updateSurvey(999L, updatedSurvey);

        // Then
        assertFalse(result.isPresent());
        verify(surveyRepository, times(1)).findById(999L);
        verify(surveyRepository, never()).save(any(Survey.class));
    }

    @Test
    void testDeleteSurvey_Found() {
        // Given
        when(surveyRepository.findById(anyLong())).thenReturn(Optional.of(survey));
        doNothing().when(surveyRepository).delete(survey);

        // When
        boolean result = surveyService.deleteSurvey(1L);

        // Then
        assertTrue(result);
        verify(surveyRepository, times(1)).findById(1L);
        verify(surveyRepository, times(1)).delete(survey);
    }

    @Test
    void testDeleteSurvey_NotFound() {
        // Given
        when(surveyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        boolean result = surveyService.deleteSurvey(999L);

        // Then
        assertFalse(result);
        verify(surveyRepository, times(1)).findById(999L);
        verify(surveyRepository, never()).deleteById(anyLong());
    }

    @Test
    void testCreateSurveyResponse() {
        // Given
        when(surveyRepository.findById(anyLong())).thenReturn(Optional.of(survey));
        when(surveyResponseRepository.save(any(SurveyResponse.class))).thenReturn(surveyResponse);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(SurveyResponse.class));

        // When
        SurveyResponse result = surveyService.createSurveyResponse(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Medium", result.getCompanySize());
        assertEquals(answers, result.getAnswers());
        assertEquals(survey, result.getSurvey());
        verify(surveyRepository, times(1)).findById(1L);
        verify(surveyResponseRepository, times(1)).save(any(SurveyResponse.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME),
                eq(RabbitMQConfig.ROUTING_KEY),
                any(SurveyResponse.class)
        );
    }

    @Test
    void testCreateSurveyResponse_SurveyNotFound() {
        // Given
        surveyResponse.setId(999L);
        surveyResponse.getSurvey().setId(999L);
        when(surveyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            surveyService.createSurveyResponse(999L,request);
        });
        verify(surveyRepository, times(1)).findById(999L);
        verify(surveyResponseRepository, never()).save(any(SurveyResponse.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(SurveyResponse.class));
    }

    @Test
    void testGetSurveyResponses() {
        // Given
        List<SurveyResponse> responses = Arrays.asList(surveyResponse);
        when(surveyResponseRepository.findBySurveyId(anyLong())).thenReturn(responses);

        // When
        List<SurveyResponse> result = surveyService.getSurveyResponses(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(surveyResponseRepository, times(1)).findBySurveyId(1L);
    }

    @Test
    void testGetSurveyResponsesPaginated() {
        // Given
        Page<SurveyResponse> page = new PageImpl<>(Arrays.asList(surveyResponse));
        Pageable pageable = PageRequest.of(0, 10);
        when(surveyResponseRepository.findBySurveyId(anyLong(), any(Pageable.class))).thenReturn(page);

        // When
        Page<SurveyResponse> result = surveyService.getSurveyResponsesPaginated(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(surveyResponseRepository, times(1)).findBySurveyId(1L, pageable);
    }
}
