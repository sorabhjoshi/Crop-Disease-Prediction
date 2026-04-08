package com.pestpredictor.dao;

import com.pestpredictor.model.PredictionHistory;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for PredictionHistory operations.
 */
public interface PredictionHistoryDao {

    PredictionHistory save(PredictionHistory history);

    Optional<PredictionHistory> findById(Long id);

    List<PredictionHistory> findByUserId(Long userId);

    List<PredictionHistory> findByUserId(Long userId, int limit);

    List<PredictionHistory> findAll();

    void delete(Long id);

    long countByUserId(Long userId);
}
