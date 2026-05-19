# 🛡️ Smart Fence Monitoring System using AI & IoT

An AI-powered Smart Fence Monitoring System that combines IoT, Artificial Intelligence, cloud computing, and mobile applications for real-time intrusion detection and remote monitoring. The system uses ESP32, PIR & vibration sensors, AI-based risk prediction, Supabase cloud storage, and an Android application to provide intelligent security monitoring with instant alerts and remote fence control.

---

# 📌 Project Overview

Traditional electric fence systems lack intelligent monitoring, remote accessibility, and real-time alerts. This project solves those limitations by integrating:

- IoT Sensors
- ESP32 Microcontroller
- AI Risk Classification
- Flask Backend APIs
- Supabase Cloud Database
- Android Mobile Application

The system continuously monitors fence activity, analyzes sensor data using AI, classifies risk levels, and instantly alerts users through mobile notifications.

---

# 🚀 Features

- 🔍 Real-time intrusion detection
- 🤖 AI-based risk classification
- 📡 ESP32 IoT integration
- ☁️ Cloud-based data storage
- 📱 Android mobile monitoring app
- 🔔 Instant push notifications
- 🎛️ Remote fence ON/OFF control
- 📊 Live sensor monitoring dashboard
- ⚡ Fast response system
- 🧠 Reduced false alarms using AI

---

# 🧠 AI Risk Classification

The system uses a Decision Tree Machine Learning model to classify intrusion levels into:

| Risk Level | Description |
|------------|-------------|
| SAFE | No intrusion detected |
| LOW | Minor disturbance |
| HIGH | Major intrusion detected |

---

# 🏗️ System Architecture

```text
PIR Sensor + Vibration Sensor
            ↓
         ESP32
            ↓
      Flask REST API
            ↓
        Supabase
            ↓
   AI Prediction Model
            ↓
   Android Mobile App
            ↓
 Notifications & Control
```

---

# 🔄 Workflow

1. Sensors detect motion or fence disturbance  
2. ESP32 processes sensor data  
3. Data is sent to Supabase cloud  
4. AI model predicts risk level  
5. Alerts are generated instantly  
6. Mobile app displays real-time updates  
7. Users can remotely control the fence  

---

# 💻 Technologies Used

| Technology | Purpose |
|------------|---------|
| Python | Backend & AI |
| Flask | REST APIs |
| ESP32 | IoT Controller |
| Supabase | Cloud Database |
| Kotlin | Android App |
| Arduino IDE | ESP32 Programming |
| Decision Tree | AI Model |
| REST APIs | Communication |

---

# 🔌 Hardware Components

- ESP32 Microcontroller
- PIR Motion Sensor
- Vibration Sensor
- Relay Module
- Buzzer
- LED Indicator
- Power Supply

---

# 📱 Mobile Application Features

- Real-time monitoring
- Live risk level display
- Instant intrusion alerts
- Cloud synchronization
- Remote fence control
- User-friendly dashboard

---

# 📂 Project Modules

## 🔹 Sensor Module
Detects motion and vibration around the fence.

## 🔹 ESP32 Processing Module
Processes sensor data and controls hardware components.

## 🔹 Communication Module
Transfers data between ESP32 and cloud using REST APIs.

## 🔹 AI Prediction Module
Analyzes sensor data and predicts risk level.

## 🔹 Cloud Storage Module
Stores sensor data, alerts, and AI predictions.

## 🔹 Mobile Application Module
Provides monitoring, notifications, and remote control.

---

# 📊 Advantages

✅ Real-time monitoring  
✅ AI-powered intelligent analysis  
✅ Reduced false alarms  
✅ Remote monitoring & control  
✅ Cloud-based storage  
✅ Fast response system  
✅ User-friendly interface  
✅ Scalable architecture  

