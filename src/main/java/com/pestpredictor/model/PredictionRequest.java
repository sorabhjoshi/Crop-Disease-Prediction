package com.pestpredictor.model;

/**
 * DTO carrying the 14 raw feature values from the prediction form.
 * Maps directly to the input expected by the Python ML model.
 */
public class PredictionRequest {

    private double temperature;
    private double humidity;
    private double windSpeed;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private double organicMatter;
    private double soilMoisture;
    private double rainfall;
    private double irrigationFrequency;
    private double waterUsageEfficiency;
    private double sunlightExposure;
    private double co2Concentration;
    private double fertilizerUsage;

    // ── Constructors ────────────────────────────────────────────────────────

    public PredictionRequest() {}

    public PredictionRequest(double temperature, double humidity, double windSpeed,
                              double nitrogen, double phosphorus, double potassium,
                              double organicMatter, double soilMoisture, double rainfall,
                              double irrigationFrequency, double waterUsageEfficiency,
                              double sunlightExposure, double co2Concentration,
                              double fertilizerUsage) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.organicMatter = organicMatter;
        this.soilMoisture = soilMoisture;
        this.rainfall = rainfall;
        this.irrigationFrequency = irrigationFrequency;
        this.waterUsageEfficiency = waterUsageEfficiency;
        this.sunlightExposure = sunlightExposure;
        this.co2Concentration = co2Concentration;
        this.fertilizerUsage = fertilizerUsage;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public double getNitrogen() { return nitrogen; }
    public void setNitrogen(double nitrogen) { this.nitrogen = nitrogen; }

    public double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }

    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }

    public double getOrganicMatter() { return organicMatter; }
    public void setOrganicMatter(double organicMatter) { this.organicMatter = organicMatter; }

    public double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(double soilMoisture) { this.soilMoisture = soilMoisture; }

    public double getRainfall() { return rainfall; }
    public void setRainfall(double rainfall) { this.rainfall = rainfall; }

    public double getIrrigationFrequency() { return irrigationFrequency; }
    public void setIrrigationFrequency(double irrigationFrequency) { this.irrigationFrequency = irrigationFrequency; }

    public double getWaterUsageEfficiency() { return waterUsageEfficiency; }
    public void setWaterUsageEfficiency(double waterUsageEfficiency) { this.waterUsageEfficiency = waterUsageEfficiency; }

    public double getSunlightExposure() { return sunlightExposure; }
    public void setSunlightExposure(double sunlightExposure) { this.sunlightExposure = sunlightExposure; }

    public double getCo2Concentration() { return co2Concentration; }
    public void setCo2Concentration(double co2Concentration) { this.co2Concentration = co2Concentration; }

    public double getFertilizerUsage() { return fertilizerUsage; }
    public void setFertilizerUsage(double fertilizerUsage) { this.fertilizerUsage = fertilizerUsage; }

    @Override
    public String toString() {
        return String.format(
                "PredictionRequest{temp=%.1f, hum=%.1f, wind=%.1f, N=%.1f, P=%.1f, K=%.1f, " +
                "om=%.1f, sm=%.1f, rain=%.1f, irr=%.1f, wue=%.2f, sun=%.1f, co2=%.1f, fert=%.1f}",
                temperature, humidity, windSpeed, nitrogen, phosphorus, potassium,
                organicMatter, soilMoisture, rainfall, irrigationFrequency,
                waterUsageEfficiency, sunlightExposure, co2Concentration, fertilizerUsage
        );
    }
}
