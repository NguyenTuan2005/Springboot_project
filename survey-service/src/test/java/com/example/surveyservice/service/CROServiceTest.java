package com.example.surveyservice.service;

import com.example.surveyservice.model.CROResult;
import com.example.surveyservice.model.CROTest;
import com.example.surveyservice.repository.CROResultRepository;
import com.example.surveyservice.repository.CROTestRepository;
import com.example.surveyservice.service.impl.CROServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CROServiceTest {

    @Mock
    private CROTestRepository croTestRepository;

    @Mock
    private CROResultRepository croResultRepository;
    private CROService croService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        croService = new CROServiceImpl(croTestRepository, croResultRepository);
    }

    @Test
    public void testCreateCROTest() {
        // Arrange
        CROTest test = new CROTest();
        test.setName("Homepage Button Test");
        test.setTargetUrl("/homepage");
        test.setVariants(Arrays.asList("Control", "Variant A", "Variant B"));

        when(croTestRepository.save(any(CROTest.class))).thenReturn(test);

        // Act
        CROTest result = croService.createTest(test);

        // Assert
        assertNotNull(result);
        assertEquals("Homepage Button Test", result.getName());
        assertEquals("/homepage", result.getTargetUrl());
        assertEquals(3, result.getVariants().size());
        verify(croTestRepository, times(1)).save(test);
    }

    @Test
    public void testGetCROTestById() {
        // Arrange
        Long testId = 1L;
        CROTest test = new CROTest();
        test.setId(testId);
        test.setName("Product Page Test");
        test.setTargetUrl("/product");
        test.setVariants(Arrays.asList("Control", "Variant A"));

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));

        // Act
        Optional<CROTest> result = croService.getTestById(testId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        assertEquals("Product Page Test", result.get().getName());
        verify(croTestRepository, times(1)).findById(testId);
    }

    @Test
    public void testGetCROTestByIdNotFound() {
        // Arrange
        Long testId = 999L;
        when(croTestRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        Optional<CROTest> result = croService.getTestById(testId);

        // Assert
        assertFalse(result.isPresent());
        verify(croTestRepository, times(1)).findById(testId);
    }

    @Test
    public void testGetAllCROTests() {
        // Arrange
        List<CROTest> tests = Arrays.asList(
                createTestWithId(1L, "Test 1", "/page1", Arrays.asList("Control", "Variant A")),
                createTestWithId(2L, "Test 2", "/page2", Arrays.asList("Control", "Variant B", "Variant C"))
        );

        when(croTestRepository.findAll()).thenReturn(tests);

        // Act
        List<CROTest> result = croService.getAllTests();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Test 1", result.get(0).getName());
        assertEquals("Test 2", result.get(1).getName());
        verify(croTestRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateCROTest() {
        // Arrange
        Long testId = 1L;
        CROTest existingTest = createTestWithId(testId, "Old Test", "/old-url", Arrays.asList("Control", "Old Variant"));
        CROTest updatedData = createTestWithId(testId, "Updated Test", "/new-url", Arrays.asList("Control", "New Variant"));

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(existingTest));
        when(croTestRepository.save(any(CROTest.class))).thenReturn(updatedData);

        // Act
        Optional<CROTest> result = croService.updateTest(testId, updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Test", result.get().getName());
        assertEquals("/new-url", result.get().getTargetUrl());
        assertEquals(Arrays.asList("Control", "New Variant"), result.get().getVariants());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croTestRepository, times(1)).save(any(CROTest.class));
    }

    @Test
    public void testUpdateCROTestNotFound() {
        // Arrange
        Long testId = 999L;
        CROTest updatedData = createTestWithId(testId, "Updated Test", "/new-url", Arrays.asList("Control", "New Variant"));

        when(croTestRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        Optional<CROTest> result = croService.updateTest(testId, updatedData);

        // Assert
        assertFalse(result.isPresent());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croTestRepository, never()).save(any(CROTest.class));
    }

    @Test
    public void testDeleteCROTest() {
        // Arrange
        Long testId = 1L;
        CROTest test = createTestWithId(testId, "Test to Delete", "/url", Arrays.asList("Control", "Variant"));

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        doNothing().when(croTestRepository).delete(test);

        // Act
        boolean result = croService.deleteTest(testId);

        // Assert
        assertTrue(result);
        verify(croTestRepository, times(1)).findById(testId);
        verify(croTestRepository, times(1)).delete(test);
    }

    @Test
    public void testDeleteCROTestNotFound() {
        // Arrange
        Long testId = 999L;
        when(croTestRepository.findById(testId)).thenReturn(Optional.empty());

        // Act
        boolean result = croService.deleteTest(testId);

        // Assert
        assertFalse(result);
        verify(croTestRepository, times(1)).findById(testId);
        verify(croTestRepository, never()).deleteById(testId);
    }

    @Test
    public void testRecordImpression() {
        // Arrange
        Long testId = 1L;
        String variant = "Variant A";
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", variant));
        CROResult existingResult = new CROResult();
        existingResult.setId(1L);
        existingResult.setTest(test);
        existingResult.setVariant(variant);
        existingResult.setImpressions(100);
        existingResult.setConversions(10);

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestIdAndVariant(testId, variant)).thenReturn(Optional.of(existingResult));
        when(croResultRepository.save(any(CROResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = croService.recordImpression(testId, variant);

        // Assert
        assertTrue(result);
        assertEquals(101, existingResult.getImpressions());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestIdAndVariant(testId, variant);
        verify(croResultRepository, times(1)).save(existingResult);
    }

    @Test
    public void testRecordImpressionNewResult() {
        // Arrange
        Long testId = 1L;
        String variant = "Variant A";
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", variant));

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestIdAndVariant(testId, variant)).thenReturn(Optional.empty());
        when(croResultRepository.save(any(CROResult.class))).thenAnswer(invocation -> {
            CROResult savedResult = invocation.getArgument(0);
            savedResult.setId(1L);
            return savedResult;
        });

        // Act
        boolean result = croService.recordImpression(testId, variant);

        // Assert
        assertTrue(result);
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestIdAndVariant(testId, variant);
        verify(croResultRepository, times(1)).save(any(CROResult.class));
    }

    @Test
    public void testRecordConversion() {
        // Arrange
        Long testId = 1L;
        String variant = "Variant A";
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", variant));
        CROResult existingResult = new CROResult();
        existingResult.setId(1L);
        existingResult.setTest(test);
        existingResult.setVariant(variant);
        existingResult.setImpressions(100);
        existingResult.setConversions(10);

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestIdAndVariant(testId, variant)).thenReturn(Optional.of(existingResult));
        when(croResultRepository.save(any(CROResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean result = croService.recordConversion(testId, variant);

        // Assert
        assertTrue(result);
        assertEquals(11, existingResult.getConversions());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestIdAndVariant(testId, variant);
        verify(croResultRepository, times(1)).save(existingResult);
    }

    @Test
    public void testGetTestResults() {
        // Arrange
        Long testId = 1L;
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", "Variant A", "Variant B"));

        List<CROResult> results = Arrays.asList(
                createResult(1L, test, "Control", 1000, 100),
                createResult(2L, test, "Variant A", 1000, 150),
                createResult(3L, test, "Variant B", 1000, 80)
        );

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestId(testId)).thenReturn(results);

        // Act
        Optional<List<CROResult>> resultOptional = croService.getTestResults(testId);

        // Assert
        assertTrue(resultOptional.isPresent());
        List<CROResult> resultList = resultOptional.get();
        assertEquals(3, resultList.size());
        assertEquals(100, resultList.get(0).getConversions());
        assertEquals(150, resultList.get(1).getConversions());
        assertEquals(80, resultList.get(2).getConversions());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestId(testId);
    }

    @Test
    public void testGetConversionRates() {
        // Arrange
        Long testId = 1L;
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", "Variant A", "Variant B"));

        List<CROResult> results = Arrays.asList(
                createResult(1L, test, "Control", 1000, 100),     // 10%
                createResult(2L, test, "Variant A", 1000, 150),   // 15%
                createResult(3L, test, "Variant B", 1000, 80)     // 8%
        );

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestId(testId)).thenReturn(results);

        // Act
        Optional<Map<String, Double>> resultOptional = croService.getConversionRates(testId);

        // Assert
        assertTrue(resultOptional.isPresent());
        Map<String, Double> rates = resultOptional.get();
        assertEquals(3, rates.size());
        assertEquals(0.1, rates.get("Control"));
        assertEquals(0.15, rates.get("Variant A"));
        assertEquals(0.08, rates.get("Variant B"));
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestId(testId);
    }

    @Test
    public void testGetWinningVariant() {
        // Arrange
        Long testId = 1L;
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", "Variant A", "Variant B"));

        List<CROResult> results = Arrays.asList(
                createResult(1L, test, "Control", 1000, 100),     // 10%
                createResult(2L, test, "Variant A", 1000, 150),   // 15% - Winner
                createResult(3L, test, "Variant B", 1000, 80)     // 8%
        );

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestId(testId)).thenReturn(results);

        // Act
        Optional<String> resultOptional = croService.getWinningVariant(testId);

        // Assert
        assertTrue(resultOptional.isPresent());
        assertEquals("Variant A", resultOptional.get());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestId(testId);
    }

    @Test
    public void testGetWinningVariantNoResults() {
        // Arrange
        Long testId = 1L;
        CROTest test = createTestWithId(testId, "Test", "/url", Arrays.asList("Control", "Variant A"));

        List<CROResult> results = Arrays.asList();

        when(croTestRepository.findById(testId)).thenReturn(Optional.of(test));
        when(croResultRepository.findByTestId(testId)).thenReturn(results);

        // Act
        Optional<String> resultOptional = croService.getWinningVariant(testId);

        // Assert
        assertFalse(resultOptional.isPresent());
        verify(croTestRepository, times(1)).findById(testId);
        verify(croResultRepository, times(1)).findByTestId(testId);
    }

    // Helper methods
    private CROTest createTestWithId(Long id, String name, String targetUrl, List<String> variants) {
        CROTest test = new CROTest();
        test.setId(id);
        test.setName(name);
        test.setTargetUrl(targetUrl);
        test.setVariants(variants);
        return test;
    }

    private CROResult createResult(Long id, CROTest test, String variant, int impressions, int conversions) {
        CROResult result = new CROResult();
        result.setId(id);
        result.setTest(test);
        result.setVariant(variant);
        result.setImpressions(impressions);
        result.setConversions(conversions);
        return result;
    }
}
