#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "universe";
const char* password = "askb0506";

const char* sensorUrl = "https://qhbqcaclkkugsbdieupz.supabase.co/rest/v1/sensor_data";
const char* aiUrl = "https://qhbqcaclkkugsbdieupz.supabase.co/rest/v1/ai_predictions?select=risk_prediction&order=created_at.desc&limit=1";

const char* apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFoYnFjYWNsa2t1Z3NiZGlldXB6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIyNDg1NjcsImV4cCI6MjA4NzgyNDU2N30.njOWdgHA8327CXP240_6qfQAZy9Xd46z-HtCAoeeUbM";   // 🔴 PUT YOUR KEY

#define PIR_PIN 13
#define VIB_PIN 34
#define BUZZER 33
#define LED 2

String deviceId = "FENCE_001";

unsigned long lastSendTime = 0;
unsigned long interval = 8000;

bool buzzerActive = false;
unsigned long buzzerStartTime = 0;

void setup() {
  Serial.begin(115200);

  pinMode(PIR_PIN, INPUT);
  pinMode(VIB_PIN, INPUT);
  pinMode(BUZZER, OUTPUT);
  pinMode(LED, OUTPUT);

  digitalWrite(BUZZER, LOW);
  digitalWrite(LED, LOW);

  WiFi.begin(ssid, password);

  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }

  Serial.println("\nWiFi Connected");
}

void loop() {

  int pir = digitalRead(PIR_PIN);

  int vib1 = analogRead(VIB_PIN);
  delay(10);
  int vib2 = analogRead(VIB_PIN);
  int vibration = (vib1 + vib2) / 2;

  Serial.print("PIR: ");
  Serial.println(pir);
  Serial.print("Vibration: ");
  Serial.println(vibration);

  float voltage = random(220, 240) / 10.0;
  float current = random(5, 10) / 10.0;

  // 🔇 Ignore noise
  if (vibration < 350) {
    if (millis() - lastSendTime > interval) {
      sendSensorData(voltage, current, pir, vibration);
      lastSendTime = millis();
    }
    delay(500);
    return;
  }

  // 📡 Send sensor data
  if (millis() - lastSendTime > interval) {
    sendSensorData(voltage, current, pir, vibration);
    lastSendTime = millis();
  }

  // 🔥 wait for DB update
  delay(2000);

  // 🤖 Get AI result (for display only)
  int risk = getAIResult();

  Serial.print("AI Risk: ");
  Serial.println(risk);

  // 🚨 BUZZER ON (PIR + MEDIUM VIBRATION)
  if (pir == 1 && vibration >700 && buzzerActive == false) {

    Serial.println("🚨 BUZZER ON (MEDIUM DETECTED)");

    digitalWrite(BUZZER, HIGH);
    digitalWrite(LED, HIGH);

    buzzerActive = true;
    buzzerStartTime = millis();
  }

  // 🔕 BUZZER OFF AFTER EXACT 6 sec
  if (buzzerActive == true && (millis() - buzzerStartTime >= 4000)) {

    Serial.println("🔕 BUZZER OFF");

    digitalWrite(BUZZER, LOW);
    digitalWrite(LED, LOW);

    buzzerActive = false;
  }

  delay(1000);
}

// 📡 SEND SENSOR DATA
void sendSensorData(float voltage, float current, int pir, int vibration) {

  if (WiFi.status() == WL_CONNECTED) {

    HTTPClient http;
    http.begin(sensorUrl);

    http.addHeader("Content-Type", "application/json");
    http.addHeader("apikey", apiKey);
    http.addHeader("Authorization", "Bearer " + String(apiKey));

    String jsonData = "{";
    jsonData += "\"device_id\":\"" + deviceId + "\",";
    jsonData += "\"voltage\":" + String(voltage) + ",";
    jsonData += "\"current\":" + String(current) + ",";
    jsonData += "\"pir_status\":" + String(pir) + ",";
    jsonData += "\"vibration_level\":" + String(vibration);
    jsonData += "}";

    http.POST(jsonData);
    http.end();
  }
}

// 🤖 GET AI RESULT
int getAIResult() {

  if (WiFi.status() == WL_CONNECTED) {

    HTTPClient http;
    http.begin(aiUrl);

    http.addHeader("apikey", apiKey);
    http.addHeader("Authorization", "Bearer " + String(apiKey));

    int code = http.GET();

    if (code == 200) {

      String payload = http.getString();

      Serial.println("RAW RESPONSE:");
      Serial.println(payload);

      if (payload.indexOf("\"risk_prediction\":2") != -1) {
        http.end();
        return 2;
      }
      else if (payload.indexOf("\"risk_prediction\":1") != -1) {
        http.end();
        return 1;
      }
    }

    http.end();
  }

  return 0;
}