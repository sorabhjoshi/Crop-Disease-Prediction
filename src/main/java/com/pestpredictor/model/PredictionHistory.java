package com.pestpredictor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PredictionHistory entity mapped to the 'prediction_history' table.
 * Records every pest prediction made by users.
 */
@Entity
@Table(name = "prediction_history")
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ── Raw input features ──────────────────────────────────────────────────

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "nitrogen")
    private Double nitrogen;

    @Column(name = "phosphorus")
    private Double phosphorus;

    @Column(name = "potassium")
    private Double potassium;

    @Column(name = "organic_matter")
    private Double organicMatter;

    @Column(name = "soil_moisture")
    private Double soilMoisture;

    @Column(name = "rainfall")
    private Double rainfall;

    @Column(name = "irrigation_frequency")
    private Double irrigationFrequency;

    @Column(name = "water_usage_efficiency")
    private Double waterUsageEfficiency;

    @Column(name = "sunlight_exposure")
    private Double sunlightExposure;

    @Column(name = "co2_concentration")
    private Double co2Concentration;

    @Column(name = "fertilizer_usage")
    private Double fertilizerUsage;

    // ── Prediction output ───────────────────────────────────────────────────

    @Column(name = "predicted_class")
    private Integer predictedClass;

    @Column(name = "predicted_label", length = 100)
    private String predictedLabel;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "probabilities", length = 500)
    private String probabilities;  // stored as JSON string

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "notes", length = 500)
    private String notes;

    // ── Lifecycle ───────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── Constructors ────────────────────────────────────────────────────────

    public PredictionHistory() {}

    // ── Getters & Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public Double getNitrogen() { return nitrogen; }
    public void setNitrogen(Double nitrogen) { this.nitrogen = nitrogen; }

    public Double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(Double phosphorus) { this.phosphorus = phosphorus; }

    public Double getPotassium() { return potassium; }
    public void setPotassium(Double potassium) { this.potassium = potassium; }

    public Double getOrganicMatter() { return organicMatter; }
    public void setOrganicMatter(Double organicMatter) { this.organicMatter = organicMatter; }

    public Double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(Double soilMoisture) { this.soilMoisture = soilMoisture; }

    public Double getRainfall() { return rainfall; }
    public void setRainfall(Double rainfall) { this.rainfall = rainfall; }

    public Double getIrrigationFrequency() { return irrigationFrequency; }
    public void setIrrigationFrequency(Double irrigationFrequency) { this.irrigationFrequency = irrigationFrequency; }

    public Double getWaterUsageEfficiency() { return waterUsageEfficiency; }
    public void setWaterUsageEfficiency(Double waterUsageEfficiency) { this.waterUsageEfficiency = waterUsageEfficiency; }

    public Double getSunlightExposure() { return sunlightExposure; }
    public void setSunlightExposure(Double sunlightExposure) { this.sunlightExposure = sunlightExposure; }

    public Double getCo2Concentration() { return co2Concentration; }
    public void setCo2Concentration(Double co2Concentration) { this.co2Concentration = co2Concentration; }

    public Double getFertilizerUsage() { return fertilizerUsage; }
    public void setFertilizerUsage(Double fertilizerUsage) { this.fertilizerUsage = fertilizerUsage; }

    public Integer getPredictedClass() { return predictedClass; }
    public void setPredictedClass(Integer predictedClass) { this.predictedClass = predictedClass; }

    public String getPredictedLabel() { return predictedLabel; }
    public void setPredictedLabel(String predictedLabel) { this.predictedLabel = predictedLabel; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getProbabilities() { return probabilities; }
    public void setProbabilities(String probabilities) { this.probabilities = probabilities; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
