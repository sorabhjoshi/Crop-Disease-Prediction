"""
Crop Pest Predictor - Python Flask API
Serves the trained Keras (.h5) + scaler (.pkl) model via REST.

Start:  python app.py
Port:   5000
"""

import os
import logging
import numpy as np
import joblib
from flask import Flask, request, jsonify

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')
log = logging.getLogger(__name__)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_DIR = os.path.join(BASE_DIR, 'model_files')

_model = None
_scaler = None
_selected_features = None


def load_artifacts():
    global _model, _scaler, _selected_features
    if _model is not None:
        return

    from tensorflow.keras.models import load_model

    _model = load_model(os.path.join(MODEL_DIR, 'pest_nn_model.h5'))
    _scaler = joblib.load(os.path.join(MODEL_DIR, 'scaler_nn.pkl'))
    _selected_features = joblib.load(os.path.join(MODEL_DIR, 'selected_features.pkl'))

    log.info("Artifacts loaded. Features: %s", _selected_features)


def create_features(d):
    eps = 1e-6
    return {
        'moist_temp_proxy': d['soil_moisture'] * d['temperature'],
        'wind_speed': d['wind_speed'],
        'water_usage_efficiency': d['water_usage_efficiency'],
        'sfi': d['organic_matter'] * (d['N'] + d['P'] + d['K']),
        'overwater_index': d['irrigation_frequency'] * d['rainfall'],
        'temperature': d['temperature'],
        'nbr': d['N'] / (d['P'] + d['K'] + eps),
        'pp': (d['sunlight_exposure'] * d['co2_concentration']) / (d['temperature'] + eps),
        'sunlight_exposure': d['sunlight_exposure'],
        'fert_heat_index': d['fertilizer_usage'] * d['temperature'],
    }


LABELS = {
    0: 'Very Low Pest Risk (0-25%)',
    1: 'Low Pest Risk (25-50%)',
    2: 'Moderate Pest Risk (50-65%)',
    3: 'High Pest Risk (65-80%)',
    4: 'Very High Pest Risk (80-100%)'
}

app = Flask(__name__)


@app.route('/predict', methods=['POST'])
def predict_endpoint():
    try:
        load_artifacts()
    except Exception as e:
        return jsonify({'error': 'Model not loaded: ' + str(e)}), 503

    body = request.get_json(silent=True)
    if not body:
        return jsonify({'error': 'Request body must be JSON'}), 400

    try:
        raw = {
            'temperature': float(body['temperature']),
            'humidity': float(body['humidity']),
            'wind_speed': float(body['wind_speed']),
            'N': float(body['N']),
            'P': float(body['P']),
            'K': float(body['K']),
            'organic_matter': float(body['organic_matter']),
            'soil_moisture': float(body['soil_moisture']),
            'rainfall': float(body['rainfall']),
            'irrigation_frequency': float(body['irrigation_frequency']),
            'water_usage_efficiency': float(body['water_usage_efficiency']),
            'sunlight_exposure': float(body['sunlight_exposure']),
            'co2_concentration': float(body['co2_concentration']),
            'fertilizer_usage': float(body['fertilizer_usage']),
        }
    except (KeyError, ValueError, TypeError) as e:
        return jsonify({'error': 'Invalid or missing field: ' + str(e)}), 400

    try:
        feats = create_features(raw)

        # ✅ SAFE FEATURE MAPPING (FIXED)
        vec = []
        for f in _selected_features:
            if f in feats:
                vec.append(feats[f])
            elif f in raw:
                vec.append(raw[f])
            else:
                log.warning("Missing feature: %s → using 0", f)
                vec.append(0.0)

        # Debug (optional)
        log.info("Vector length: %d", len(vec))

        scaled = _scaler.transform([vec])
        probs = _model.predict(scaled, verbose=0)

        cls = int(np.argmax(probs))
        conf = float(probs[0][cls])

        log.info("Prediction: class=%d, conf=%.4f", cls, conf)

        return jsonify({
            'predicted_class': cls,
            'predicted_label': LABELS.get(cls, "Unknown"),
            'confidence': round(conf, 4),
            'probabilities': [round(float(p), 4) for p in probs[0]]
        })

    except Exception as e:
        log.error("Prediction error: %s", e, exc_info=True)
        return jsonify({'error': 'Prediction failed: ' + str(e)}), 500


@app.route('/health')
def health():
    return jsonify({'status': 'UP', 'service': 'CropPestPredictor-PythonAPI'})


if __name__ == '__main__':
    log.info("Starting Flask API on port 5000...")
    app.run(host='0.0.0.0', port=5000, debug=False)