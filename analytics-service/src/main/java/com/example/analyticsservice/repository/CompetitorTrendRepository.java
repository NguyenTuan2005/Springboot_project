package com.example.analyticsservice.repository;

import com.example.analyticsservice.model.CompetitorTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompetitorTrendRepository extends JpaRepository<CompetitorTrend, Long> {
    List<CompetitorTrend> findByKeywordOrderByDateDesc(String keyword);
}

