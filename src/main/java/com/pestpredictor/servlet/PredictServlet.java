package com.pestpredictor.servlet;

import com.pestpredictor.model.PredictionRequest;
import com.pestpredictor.model.PredictionResult;
import com.pestpredictor.model.User;
import com.pestpredictor.service.PredictionService;
import com.pestpredictor.util.SessionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet({"/predict", "/predict/result"})
public class PredictServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PredictServlet.class.getName());
    private final PredictionService predictionService = new PredictionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String path = request.getServletPath();

        if ("/predict/result".equals(path)) {
            // ✅ Check if result exists
            if (request.getSession().getAttribute("lastResult") == null) {
                response.sendRedirect(request.getContextPath() + "/predict.html");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/result.html");
        } else {
            response.sendRedirect(request.getContextPath() + "/predict.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        User user = SessionUtil.getLoggedInUser(request);

        try {
            PredictionRequest predReq = extractRequest(request);

            String validationError = validateInputs(predReq);
            if (validationError != null) {
                response.sendRedirect(request.getContextPath() + "/predict.html?error=" + validationError);
                return;
            }

            PredictionResult result = predictionService.predict(predReq);

            if (!result.isSuccess()) {
                response.sendRedirect(request.getContextPath() + "/predict.html?error=" + result.getErrorMessage());
                return;
            }

            // ✅ Save in DB
            predictionService.savePrediction(user, predReq, result);

            // ✅ Store in session
            request.getSession().setAttribute("lastResult", result);
            request.getSession().setAttribute("lastRequest", predReq);

            logger.info("Prediction success for user: " + user.getUsername());

            response.sendRedirect(request.getContextPath() + "/predict/result");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/predict.html?error=Invalid input values");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Prediction error", e);
            response.sendRedirect(request.getContextPath() + "/predict.html?error=Something went wrong");
        }
    }

    private PredictionRequest extractRequest(HttpServletRequest req) {
        PredictionRequest pr = new PredictionRequest();
        pr.setTemperature(Double.parseDouble(req.getParameter("temperature")));
        pr.setHumidity(Double.parseDouble(req.getParameter("humidity")));
        pr.setWindSpeed(Double.parseDouble(req.getParameter("windSpeed")));
        pr.setNitrogen(Double.parseDouble(req.getParameter("nitrogen")));
        pr.setPhosphorus(Double.parseDouble(req.getParameter("phosphorus")));
        pr.setPotassium(Double.parseDouble(req.getParameter("potassium")));
        pr.setOrganicMatter(Double.parseDouble(req.getParameter("organicMatter")));
        pr.setSoilMoisture(Double.parseDouble(req.getParameter("soilMoisture")));
        pr.setRainfall(Double.parseDouble(req.getParameter("rainfall")));
        pr.setIrrigationFrequency(Double.parseDouble(req.getParameter("irrigationFrequency")));
        pr.setWaterUsageEfficiency(Double.parseDouble(req.getParameter("waterUsageEfficiency")));
        pr.setSunlightExposure(Double.parseDouble(req.getParameter("sunlightExposure")));
        pr.setCo2Concentration(Double.parseDouble(req.getParameter("co2Concentration")));
        pr.setFertilizerUsage(Double.parseDouble(req.getParameter("fertilizerUsage")));
        return pr;
    }

    private String validateInputs(PredictionRequest req) {
        if (req.getTemperature() < -10 || req.getTemperature() > 60)
            return "Temperature must be between -10 and 60";
        if (req.getHumidity() < 0 || req.getHumidity() > 100)
            return "Humidity must be 0-100";
        return null;
    }
}