from flask import Flask, jsonify
import pandas as pd
import joblib
import requests
import time

app = Flask(__name__)

# 🔹 Model
model = None
last_alert_sent = None

def get_model():
    global model
    if model is not None:
        return model
    model = joblib.load("fence_model_3level.pkl")
    print("✅ AI Model Loaded Successfully")
    return model

# 🔹 URLs
SUPABASE_SENSOR_URL = "https://qhbqcaclkkugsbdieupz.supabase.co/rest/v1/sensor_data?order=created_at.desc&limit=1"
SUPABASE_ALERT_URL = "https://qhbqcaclkkugsbdieupz.supabase.co/rest/v1/alerts"
SUPABASE_AI_URL = "https://qhbqcaclkkugsbdieupz.supabase.co/rest/v1/ai_predictions"

API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFoYnFjYWNsa2t1Z3NiZGlldXB6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIyNDg1NjcsImV4cCI6MjA4NzgyNDU2N30.njOWdgHA8327CXP240_6qfQAZy9Xd46z-HtCAoeeUbM"   # 🔴 ADD KEY

headers = {
    "apikey": API_KEY,
    "Authorization": f"Bearer {API_KEY}",
    "Content-Type": "application/json"
}

# 🔹 SAFE REQUEST (RETRY)
def safe_get(url):
    for i in range(3):
        try:
            res = requests.get(url, headers=headers, timeout=10)
            if res.status_code == 200:
                return res
        except Exception as e:
            print(f"Retry {i+1}:", e)
            time.sleep(1)
    return None

def safe_post(url, data):
    try:
        requests.post(url, headers=headers, json=data, timeout=10)
    except Exception as e:
        print("POST ERROR:", e)

# 🔹 Label
def interpret_prediction(pred):
    return ["SAFE", "LOW", "HIGH"][pred] if pred in [0,1,2] else "UNKNOWN"

@app.route("/")
def home():
    return "✅ Smart Fence AI RUNNING"

# 🔥 MAIN
@app.route("/auto_predict")
def auto_predict():

    global last_alert_sent

    model = get_model()

    # 🔹 GET DATA (SAFE)
    response = safe_get(SUPABASE_SENSOR_URL)

    if response is None:
        return jsonify({"error": "Supabase not responding"}), 500

    data = response.json()

    if not data:
        return jsonify({"error": "No data"}), 404

    latest = data[0]

    # 🔹 Extract
    vibration = int(latest["vibration_level"])
    pir = int(latest["pir_status"])
    voltage = float(latest["voltage"])
    current = float(latest["current"])

    # 🔹 AI Input
    input_data = pd.DataFrame([{
        "voltage": voltage,
        "current": current,
        "pir_status": pir,
        "vibration_level": vibration
    }])

    # 🔹 Predict
    try:
        prediction = int(model.predict(input_data)[0])
    except:
        prediction = 0

    # 🔥 FINAL LOGIC (VERY IMPORTANT)
    if vibration < 350:
        prediction = 0
    elif pir == 1 and vibration >= 700:
        prediction = 2
    elif pir == 1 and 350 <= vibration < 700:
        prediction = 1
    else:
        prediction = 0

    intrusion_status = interpret_prediction(prediction)

    # 🔎 DEBUG
    print("\n------ DEBUG ------")
    print("PIR:", pir)
    print("Vibration:", vibration)
    print("Final:", intrusion_status)
    print("-------------------\n")

    # 🔹 STORE AI
    ai_data = {
        "device_id": latest["device_id"],
        "risk_prediction": prediction,
        "ai_intrusion_status": intrusion_status
    }

    safe_post(SUPABASE_AI_URL, ai_data)

    # 🚨 ALERT (ONLY HIGH)
    if prediction == 2 and last_alert_sent != "HIGH":

        alert_data = {
            "device_id": latest["device_id"],
            "title": "Fence Intrusion",
            "message": "HIGH risk detected - Immediate action required!",
            "alert_type": "Intrusion",
            "severity": "HIGH",
            "risk_level": "HIGH",
            "status": "NEW"
        }

        safe_post(SUPABASE_ALERT_URL, alert_data)

        print("🚨 ALERT SENT")

        last_alert_sent = "HIGH"

    # 🔁 RESET
    if prediction == 0:
        last_alert_sent = None

    return jsonify({
        "ai_intrusion_status": intrusion_status,
        "risk_prediction": prediction,
        "sensor": {
            "pir": pir,
            "vibration": vibration,
            "voltage": voltage,
            "current": current
        }
    })

# 🔹 RUN
if __name__ == "__main__":
    print("\n🚀 Running...")
    print("http://127.0.0.1:5000/auto_predict\n")
    app.run(debug=True)