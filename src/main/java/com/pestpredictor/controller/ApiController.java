package com.pestpredictor.controller;

import com.pestpredictor.model.PredictionHistory;
import com.pestpredictor.model.PredictionResult;
import com.pestpredictor.model.User;
import com.pestpredictor.service.PredictionService;
import com.pestpredictor.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    // ✅ FIX: Inject service (DO NOT create manually)
    private final PredictionService predictionService;

    public ApiController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpServletRequest request) {
        User user = SessionUtil.getLoggedInUser(request);
        if (user == null)
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        List<PredictionHistory> all = predictionService.getHistoryByUser(user.getId());
        List<PredictionHistory> recent = predictionService.getRecentHistoryByUser(user.getId(), 5);

        long high = all.stream()
                .filter(h -> h.getPredictedClass() != null && h.getPredictedClass() >= 3)
                .count();

        long low = all.stream()
                .filter(h -> h.getPredictedClass() != null && h.getPredictedClass() <= 1)
                .count();

        String lastDate = recent.isEmpty() ? null :
                recent.get(0).getCreatedAt() != null
                        ? recent.get(0).getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                        : null;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("username", user.getUsername());
        resp.put("fullName", user.getFullName());
        resp.put("totalPredictions", all.size());
        resp.put("highRiskCount", high);
        resp.put("lowRiskCount", low);
        resp.put("lastPredictionDate", lastDate != null ? lastDate : "N/A");
        resp.put("recentPredictions", recent.stream().map(this::toMap).toList());

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/last-result")
    public ResponseEntity<?> lastResult(HttpServletRequest request) {
        if (!SessionUtil.isLoggedIn(request))
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        PredictionResult r = (PredictionResult) request.getSession().getAttribute("lastResult");

        if (r == null)
            return ResponseEntity.status(404).body(Map.of("error", "No result in session"));

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("predictedClass", r.getPredictedClass());
        m.put("predictedLabel", r.getPredictedLabel());
        m.put("confidenceScore", r.getConfidenceScore());
        m.put("probabilities", r.getProbabilities());

        return ResponseEntity.ok(m);
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(HttpServletRequest request) {
        User user = SessionUtil.getLoggedInUser(request);

        if (user == null)
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        return ResponseEntity.ok(
                predictionService.getHistoryByUser(user.getId())
                        .stream()
                        .map(this::toMap)
                        .toList()
        );
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "app", "CropPestPredictor"));
    }

    private Map<String, Object> toMap(PredictionHistory h) {
        Map<String, Object> m = new LinkedHashMap<>();

        m.put("id", h.getId());
        m.put("temperature", h.getTemperature());
        m.put("humidity", h.getHumidity());
        m.put("windSpeed", h.getWindSpeed());
        m.put("nitrogen", h.getNitrogen());
        m.put("phosphorus", h.getPhosphorus());
        m.put("potassium", h.getPotassium());
        m.put("organicMatter", h.getOrganicMatter());
        m.put("soilMoisture", h.getSoilMoisture());
        m.put("rainfall", h.getRainfall());
        m.put("irrigationFrequency", h.getIrrigationFrequency());
        m.put("waterUsageEfficiency", h.getWaterUsageEfficiency());
        m.put("sunlightExposure", h.getSunlightExposure());
        m.put("co2Concentration", h.getCo2Concentration());
        m.put("fertilizerUsage", h.getFertilizerUsage());
        m.put("predictedClass", h.getPredictedClass());
        m.put("predictedLabel", h.getPredictedLabel());
        m.put("confidenceScore", h.getConfidenceScore());
        m.put("probabilities", h.getProbabilities());

        m.put("createdAt",
                h.getCreatedAt() != null
                        ? h.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : null
        );

        return m;
    }
}