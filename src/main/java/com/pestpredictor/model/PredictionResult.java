package com.pestpredictor.model;

import java.util.List;

/**
 * DTO holding the result from the ML prediction endpoint.
 */
public class PredictionResult {

    private int predictedClass;
    private String predictedLabel;
    private double confidenceScore;
    private List<Double> probabilities;
    private String errorMessage;
    private boolean success;

    // ── Static factory methods ──────────────────────────────────────────────

    public static PredictionResult success(int predictedClass, String predictedLabel,
                                            double confidenceScore, List<Double> probabilities) {
        PredictionResult result = new PredictionResult();
        result.predictedClass = predictedClass;
        result.predictedLabel = predictedLabel;
        result.confidenceScore = confidenceScore;
        result.probabilities = probabilities;
        result.success = true;
        return result;
    }

    public static PredictionResult failure(String errorMessage) {
        PredictionResult result = new PredictionResult();
        result.errorMessage = errorMessage;
        result.success = false;
        return result;
    }

    // ── Constructors ────────────────────────────────────────────────────────

    public PredictionResult() {}

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int getPredictedClass() { return predictedClass; }
    public void setPredictedClass(int predictedClass) { this.predictedClass = predictedClass; }

    public String getPredictedLabel() { return predictedLabel; }
    public void setPredictedLabel(String predictedLabel) { this.predictedLabel = predictedLabel; }

    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }

    public List<Double> getProbabilities() { return probabilities; }
    public void setProbabilities(List<Double> probabilities) { this.probabilities = probabilities; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    /**
     * Returns a CSS class based on the risk level for visual styling.
     */
    public String getRiskCssClass() {
        return switch (predictedClass) {
            case 0 -> "risk-very-low";
            case 1 -> "risk-low";
            case 2 -> "risk-moderate";
            case 3 -> "risk-high";
            case 4 -> "risk-very-high";
            default -> "risk-unknown";
        };
    }

    /**
     * Returns a color hex code matching the risk level.
     */
    public String getRiskColor() {
        return switch (predictedClass) {
            case 0 -> "#22c55e";
            case 1 -> "#86efac";
            case 2 -> "#f59e0b";
            case 3 -> "#f97316";
            case 4 -> "#ef4444";
            default -> "#94a3b8";
        };
    }
}
