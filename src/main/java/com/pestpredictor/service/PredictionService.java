package com.pestpredictor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pestpredictor.dao.PredictionHistoryDao;
import com.pestpredictor.dao.PredictionHistoryDaoImpl;
import com.pestpredictor.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PredictionService {

    private static final Logger logger = Logger.getLogger(PredictionService.class.getName());

    // ✅ FIXED URL
    private static final String PYTHON_URL = "http://localhost:5000/predict";

    private final PredictionHistoryDao historyDao;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public PredictionService() {
        this.historyDao = new PredictionHistoryDaoImpl();
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // ===================== PREDICTION =====================

    public PredictionResult predict(PredictionRequest request) {
        try {
            String jsonBody = buildRequestJson(request);

            logger.info("Calling Python API URL: " + PYTHON_URL);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(PYTHON_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                return PredictionResult.failure("Python API error");
            }

            return parseResponse(response.body());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Prediction failed", e);
            return PredictionResult.failure(e.getMessage());
        }
    }

    // ===================== HISTORY METHODS (FIXED) =====================

    public List<PredictionHistory> getHistoryByUser(Long userId) {
        return historyDao.findByUserId(userId);
    }

    public List<PredictionHistory> getRecentHistoryByUser(Long userId, int limit) {
        return historyDao.findByUserId(userId, limit);
    }

    public long countByUser(Long userId) {
        return historyDao.countByUserId(userId);
    }

    public PredictionHistory savePrediction(User user, PredictionRequest request, PredictionResult result) {
        PredictionHistory history = new PredictionHistory();

        history.setUser(user);
        history.setTemperature(request.getTemperature());
        history.setHumidity(request.getHumidity());
        history.setWindSpeed(request.getWindSpeed());
        history.setNitrogen(request.getNitrogen());
        history.setPhosphorus(request.getPhosphorus());
        history.setPotassium(request.getPotassium());
        history.setOrganicMatter(request.getOrganicMatter());
        history.setSoilMoisture(request.getSoilMoisture());
        history.setRainfall(request.getRainfall());
        history.setIrrigationFrequency(request.getIrrigationFrequency());
        history.setWaterUsageEfficiency(request.getWaterUsageEfficiency());
        history.setSunlightExposure(request.getSunlightExposure());
        history.setCo2Concentration(request.getCo2Concentration());
        history.setFertilizerUsage(request.getFertilizerUsage());

        history.setPredictedClass(result.getPredictedClass());
        history.setPredictedLabel(result.getPredictedLabel());
        history.setConfidenceScore(result.getConfidenceScore());

        if (result.getProbabilities() != null) {
            history.setProbabilities(result.getProbabilities().toString());
        }

        return historyDao.save(history);
    }

    // ===================== JSON =====================

    private String buildRequestJson(PredictionRequest req) throws IOException {
        return objectMapper.writeValueAsString(new LinkedHashMap<>() {{
            put("temperature", req.getTemperature());
            put("humidity", req.getHumidity());
            put("wind_speed", req.getWindSpeed());
            put("N", req.getNitrogen());
            put("P", req.getPhosphorus());
            put("K", req.getPotassium());
            put("organic_matter", req.getOrganicMatter());
            put("soil_moisture", req.getSoilMoisture());
            put("rainfall", req.getRainfall());
            put("irrigation_frequency", req.getIrrigationFrequency());
            put("water_usage_efficiency", req.getWaterUsageEfficiency());
            put("sunlight_exposure", req.getSunlightExposure());
            put("co2_concentration", req.getCo2Concentration());
            put("fertilizer_usage", req.getFertilizerUsage());
        }});
    }

    private PredictionResult parseResponse(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);

        if (root.has("error")) {
            return PredictionResult.failure(root.get("error").asText());
        }

        int predictedClass = root.get("predicted_class").asInt();
        String predictedLabel = root.get("predicted_label").asText();
        double confidenceScore = root.get("confidence").asDouble();

        List<Double> probs = new ArrayList<>();
        root.get("probabilities").forEach(p -> probs.add(p.asDouble()));

        return PredictionResult.success(predictedClass, predictedLabel, confidenceScore, probs);
    }
}