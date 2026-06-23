# 🏥 QUEUE SOLVED - Clinic Queue Management System

A production-ready clinic queue management system with real-time updates, dynamic ETA predictions, emergency priority, and self-registration via QR code.

![QUEUE SOLVED](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)
![React](https://img.shields.io/badge/React-18-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green)
![Railway](https://img.shields.io/badge/Railway-Deployed-blueviolet)
![Vercel](https://img.shields.io/badge/Vercel-Deployed-black)

---

## 📋 Overview

**QUEUE SOLVED** is a modern clinic queue management system that eliminates paper tokens and provides real-time queue visibility. It features a **Receptionist Dashboard**, **Waiting Room TV Display**, **QR-based Self-Registration**, and **Emergency Priority Queue**.

### 🎯 Key Features

| Feature | Description |
|---------|-------------|
| **Real-time Queue Management** | Add, call, skip, complete, or delete patients |
| **Automatic Token Generation** | Sequential tokens (A001, A002, A003...) |
| **Live TV Display** | Shows current consulting patient and upcoming tokens |
| **WebSocket Updates** | Instant synchronization between all screens |
| **Dynamic ETA Calculation** | Uses disease averages from database |
| **QR Self-Registration** | Patients can register by scanning QR code on TV |
| **Emergency Priority** | Critical patients moved to front of queue |
| **Voice Announcements** | Automatic voice alerts when calling patients |
| **Doctor Analytics** | Track total patients, completed, waiting, and averages |
| **Secure Login** | Protected receptionist dashboard |
| **Patient Search** | Search patients by phone number |

---

## 🛠️ Technology Stack

### Backend
Java 21

Spring Boot 3.1.5

Spring Data MongoDB

Spring WebSocket

Spring Web

Lombok

Maven 3.8+

text

### Frontend
React 18

JavaScript ES6+

CSS3 (Responsive Design)

Axios (HTTP Client)

SockJS + STOMP (WebSocket)

React Router (Navigation)

React Toastify (Notifications)

React Icons

QRCode.react

text

### Database
MongoDB Atlas (Cloud)

text

### Deployment
Backend: Railway

Frontend: Vercel

text

---

## 🚀 Live Demo

| Service | URL |
|---------|-----|
| **Frontend** | `https://your-vercel-app.vercel.app` |
| **Backend API** | `https://smart-clinic-queue-production.up.railway.app` |
| **Health Check** | `https://smart-clinic-queue-production.up.railway.app/health` |

**Login Credentials:**
- Username: `receptionist`
- Password: `clinic123`

---

## 📁 Project Structure

```text
smartclinic-queue/
├── backend/
│   ├── src/main/java/com/smartclinic/
│   │   ├── SmartClinicApplication.java
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   └── MongoDBConfig.java
│   │   ├── controller/
│   │   │   ├── PatientController.java
│   │   │   ├── ETAController.java
│   │   │   ├── SelfRegistrationController.java
│   │   │   ├── HealthController.java
│   │   │   └── TestController.java
│   │   ├── model/
│   │   │   ├── Patient.java
│   │   │   ├── PatientStatus.java
│   │   │   ├── DiseaseStat.java
│   │   │   ├── DoctorStat.java
│   │   │   ├── ConsultationHistory.java
│   │   │   └── MLMetadata.java
│   │   ├── repository/
│   │   │   ├── PatientRepository.java
│   │   │   ├── DiseaseStatRepository.java
│   │   │   ├── DoctorStatRepository.java
│   │   │   ├── ConsultationHistoryRepository.java
│   │   │   └── MLMetadataRepository.java
│   │   ├── service/
│   │   │   ├── PatientService.java
│   │   │   ├── QueueWebSocketService.java
│   │   │   ├── ETAService.java
│   │   │   ├── MLPredictionService.java
│   │   │   └── StatisticsService.java
│   │   ├── dto/
│   │   │   ├── PatientRequest.java
│   │   │   ├── PatientResponse.java
│   │   │   ├── QueueResponse.java
│   │   │   ├── ETAResponse.java
│   │   │   ├── SelfRegistrationRequest.java
│   │   │   └── SelfRegistrationResponse.java
│   │   └── exception/
│   │       ├── PatientNotFoundException.java
│   │       └── GlobalExceptionHandler.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml

├── frontend/
│   ├── src/
│   │   ├── index.js
│   │   ├── index.css
│   │   ├── App.js
│   │   ├── App.css
│   │   ├── components/
│   │   │   ├── Login.jsx
│   │   │   ├── ReceptionistDashboard.jsx
│   │   │   ├── PatientForm.jsx
│   │   │   ├── QueueTable.jsx
│   │   │   ├── TVDisplay.jsx
│   │   │   ├── CurrentPatientCard.jsx
│   │   │   ├── UpcomingTokens.jsx
│   │   │   ├── SelfRegistration.jsx
│   │   │   ├── DoctorAnalytics.jsx
│   │   │   └── VoiceAnnouncement.jsx
│   │   ├── services/
│   │   │   ├── api.js
│   │   │   └── websocket.js
│   │   └── styles/
│   │       ├── dashboard.css
│   │       ├── tvdisplay.css
│   │       ├── queue.css
│   │       ├── login.css
│   │       └── self-registration.css
│   ├── package.json
│   └── .env.production

└── README.md
```

---

## 🗄️ Database Schema

### 1. patients
```javascript
{
  _id: ObjectId,
  token: "A001",
  name: "Ram Kumar",
  phone: "9876543210",
  disease: "Fever",
  status: "WAITING",
  consultationDuration: 8,
  estimatedTime: "10:45 AM",
  queuePosition: 1,
  createdAt: Date,
  isEmergency: false,
  emergencyReason: null,
  priorityApproved: false
}
2. disease_statistics
javascript
{
  _id: ObjectId,
  disease: "Fever",
  averageDuration: 8.5,
  minDuration: 3,
  maxDuration: 12,
  totalPatients: 45,
  lastUpdated: Date
}
3. doctor_statistics
javascript
{
  _id: ObjectId,
  doctor: "Dr. Arun",
  averageDuration: 12.5,
  minDuration: 5,
  maxDuration: 22,
  totalPatients: 42,
  lastUpdated: Date
}
4. consultation_history
javascript
{
  _id: ObjectId,
  patientId: "PAT001",
  token: "A001",
  age: 35,
  gender: "Male",
  doctor: "Dr. Arun",
  disease: "Fever",
  dayOfWeek: "Monday",
  timeSlot: "Morning",
  predictedDuration: 8,
  actualDuration: 9,
  queueLength: 2,
  createdAt: Date
}
##🔌 API Endpoints
Patient Management
Method	Endpoint	Description
POST	/api/patient	Add new patient
GET	/api/patient/queue	Get current queue
POST	/api/patient/next	Call next patient
POST	/api/patient/{id}/skip	Skip a patient
POST	/api/patient/{id}/complete	Complete consultation
DELETE	/api/patient/{id}	Delete a patient
GET	/api/patient/search?phone=	Search by phone
GET	/api/patient/consulting	Get current consulting patient
Self-Registration
Method	Endpoint	Description
POST	/api/self-register	Register via QR code
ETA & ML
Method	Endpoint	Description
GET	/api/eta/{token}	Get ETA for a patient
GET	/api/eta/statistics/disease	Get disease statistics
GET	/api/eta/statistics/doctor	Get doctor statistics
Health Check
Method	Endpoint	Description
GET	/health	Check if backend is running
GET	/	Welcome message
WebSocket
Endpoint	Description
/ws	WebSocket endpoint
/topic/queue	Queue updates

🌐 Live Demo

URL:
https://smart-clinic-queue-git-main-shriadhithyat-5905s-projects.vercel.app/

Demo Credentials
Role	Username	Password
Receptionist	receptionist	clinic123
🚀 Features
🩺 Receptionist Dashboard
Add patients with:
Name
Phone Number
Disease
Symptoms
Automatic token generation
Emergency symptom detection with priority approval
Queue management:
Call Next
Skip Patient
Complete Consultation
Delete Patient
Doctor and queue analytics dashboard
📺 TV Display
Shows currently consulting patient
Displays estimated consultation time
Shows upcoming tokens with ETA
Emergency badge for priority patients
QR code for self-registration
Fullscreen mode for waiting rooms
Real-time updates using WebSockets
📱 Self Registration

Patients or guardians can:

Scan QR code from TV display
Fill patient details
Get token instantly
View estimated waiting time
Track queue status in real time
🚨 Emergency Priority Queue

The system automatically detects emergency symptoms such as:

Chest Pain
Breathing Difficulty
Heavy Bleeding
Stroke Symptoms
Severe Burns
High Fever with Seizures
Workflow
Receptionist enters symptoms.
System detects emergency symptoms.
Popup asks for priority approval.
Patient is moved to the front of the queue.
TV display shows emergency badge.
Voice assistant announces emergency patient.
🔊 Voice Assistant

English voice announcements for:

Current token
Next patient
Emergency patient alerts

Example:

"Token A016, please proceed to the consultation room."

"Emergency patient A025, please proceed immediately."

🤖 AI ETA Prediction

The system predicts consultation duration using:

Disease-wise consultation history
MongoDB historical records
Continuously updated average consultation duration

Features:

Dynamic ETA prediction
Auto-learning from completed consultations
Real-time ETA recalculation
Queue health monitoring
🔊 Voice Announcements
Automatic when "Call Next" is clicked

"Token Number A016, please proceed to the consultation room"

Emergency: "Emergency patient A025, please proceed immediately"

Toggle ON/OFF via speaker icon

📊 ETA Prediction
How It Works
Patient selects disease

System looks up disease average

Calculates estimated time based on queue position

Updates in real-time as patients complete

Confidence Intervals
Shows wait time as range: 15 ± 5 mins

Probabilities: within 10min, 20min, 30min
📝 License
This project is licensed under the MIT License.

🤝 Contributing
Fork the repository

Create your feature branch: git checkout -b feature/AmazingFeature

Commit: git commit -m 'Add some AmazingFeature'

Push: git push origin feature/AmazingFeature

Open a Pull Request

🎉 Acknowledgments
Built with ❤️ using Spring Boot and React

Real-time updates powered by WebSocket

Deployed on Railway and Vercel

