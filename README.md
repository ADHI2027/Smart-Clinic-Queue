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
smartclinic-queue/
├── backend/
│ ├── src/main/java/com/smartclinic/
│ │ ├── SmartClinicApplication.java
│ │ ├── config/
│ │ │ ├── CorsConfig.java
│ │ │ ├── WebSocketConfig.java
│ │ │ └── MongoDBConfig.java
│ │ ├── controller/
│ │ │ ├── PatientController.java
│ │ │ ├── ETAController.java
│ │ │ ├── SelfRegistrationController.java
│ │ │ ├── HealthController.java
│ │ │ └── TestController.java
│ │ ├── model/
│ │ │ ├── Patient.java
│ │ │ ├── PatientStatus.java
│ │ │ ├── DiseaseStat.java
│ │ │ ├── DoctorStat.java
│ │ │ ├── ConsultationHistory.java
│ │ │ └── MLMetadata.java
│ │ ├── repository/
│ │ │ ├── PatientRepository.java
│ │ │ ├── DiseaseStatRepository.java
│ │ │ ├── DoctorStatRepository.java
│ │ │ ├── ConsultationHistoryRepository.java
│ │ │ └── MLMetadataRepository.java
│ │ ├── service/
│ │ │ ├── PatientService.java
│ │ │ ├── QueueWebSocketService.java
│ │ │ ├── ETAService.java
│ │ │ ├── MLPredictionService.java
│ │ │ └── StatisticsService.java
│ │ ├── dto/
│ │ │ ├── PatientRequest.java
│ │ │ ├── PatientResponse.java
│ │ │ ├── QueueResponse.java
│ │ │ ├── ETAResponse.java
│ │ │ ├── SelfRegistrationRequest.java
│ │ │ └── SelfRegistrationResponse.java
│ │ └── exception/
│ │ ├── PatientNotFoundException.java
│ │ └── GlobalExceptionHandler.java
│ ├── src/main/resources/
│ │ └── application.properties
│ └── pom.xml
├── frontend/
│ ├── src/
│ │ ├── index.js
│ │ ├── index.css
│ │ ├── App.js
│ │ ├── App.css
│ │ ├── components/
│ │ │ ├── Login.jsx
│ │ │ ├── ReceptionistDashboard.jsx
│ │ │ ├── PatientForm.jsx
│ │ │ ├── QueueTable.jsx
│ │ │ ├── TVDisplay.jsx
│ │ │ ├── CurrentPatientCard.jsx
│ │ │ ├── UpcomingTokens.jsx
│ │ │ ├── SelfRegistration.jsx
│ │ │ ├── DoctorAnalytics.jsx
│ │ │ └── VoiceAnnouncement.jsx
│ │ ├── services/
│ │ │ ├── api.js
│ │ │ └── websocket.js
│ │ └── styles/
│ │ ├── dashboard.css
│ │ ├── tvdisplay.css
│ │ ├── queue.css
│ │ ├── login.css
│ │ └── self-registration.css
│ ├── package.json
│ └── .env.production
└── README.md

text

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
🔌 API Endpoints
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
🚀 Installation & Setup
Prerequisites
Java 21 or higher

Node.js 16+ and npm

MongoDB Atlas account

Git

Step 1: Clone the Repository
bash
git clone https://github.com/your-username/smartclinic-queue.git
cd smartclinic-queue
Step 2: MongoDB Atlas Setup
Create a free cluster at MongoDB Atlas

Create a database user

Whitelist IP addresses (0.0.0.0/0 for testing)

Get your connection string:

text
mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/smartclinic_queue?retryWrites=true&w=majority
Step 3: Backend Deployment (Railway)
Go to Railway

Click "New Project" → "Deploy from GitHub"

Select your repository

Add environment variables:

Variable	Value
MONGODB_URI	Your MongoDB Atlas connection string
PORT	8080
Deploy and get your URL: https://your-app.up.railway.app

Step 4: Frontend Deployment (Vercel)
Go to Vercel

Click "Add New" → "Project"

Select your repository

Add environment variables:

Variable	Value
REACT_APP_API_URL	https://your-app.up.railway.app
REACT_APP_WS_URL	wss://your-app.up.railway.app/ws
REACT_APP_BASE_URL	https://your-vercel-app.vercel.app
Deploy

Step 5: Local Development
Start MongoDB:

bash
cd "C:\Program Files\MongoDB\Server\8.3\bin"
mongod.exe --dbpath "C:\data\db"
Start Backend:

bash
cd backend
mvn spring-boot:run
Start Frontend:

bash
cd frontend
npm start
🎮 How to Use
Login
text
URL: http://localhost:3000
Username: receptionist
Password: clinic123
Receptionist Dashboard
Add patients with name, phone, disease, and symptoms

Emergency symptoms auto-detect and prompt for priority

Queue management with Call Next, Skip, Complete, Delete

Analytics dashboard

TV Display
Shows currently consulting patient with ETA

Shows upcoming tokens with estimated times

QR code for self-registration

Fullscreen mode

Self-Registration
Scan QR code on TV display

Fill in patient details

Get token and estimated wait time

Track queue in real-time

🚨 Emergency Priority System
The system automatically detects emergency symptoms:

Emergency Symptoms	Action
Chest Pain	🚨 Priority Queue
Breathing Difficulty	🚨 Priority Queue
Heavy Bleeding	🚨 Priority Queue
Stroke Symptoms	🚨 Priority Queue
Severe Burns	🚨 Priority Queue
Seizures	🚨 Priority Queue
High Fever	🚨 Priority Queue
Flow:

Receptionist enters symptoms

System detects emergency

Popup asks for priority approval

Patient moved to front of queue

TV displays emergency badge

Voice announcement for emergency

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

🛠️ Troubleshooting
Backend Not Starting
bash
cd backend
mvn clean compile
mvn spring-boot:run
Frontend Not Starting
bash
cd frontend
npm install
npm start
MongoDB Connection Failed
Check MongoDB URI in Railway variables

Whitelist IP in MongoDB Atlas

Check network access

WebSocket Connection Failed
Update REACT_APP_WS_URL in Vercel

Check CORS configuration

📝 License
This project is licensed under the MIT License.

🤝 Contributing
Fork the repository

Create your feature branch: git checkout -b feature/AmazingFeature

Commit: git commit -m 'Add some AmazingFeature'

Push: git push origin feature/AmazingFeature

Open a Pull Request

📧 Support
For support, email: support@queuesolved.com

🎉 Acknowledgments
Built with ❤️ using Spring Boot and React

Real-time updates powered by WebSocket

Deployed on Railway and Vercel

📌 Quick Reference
Commands
bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm start

# Deploy Backend
git push origin main

# Deploy Frontend (Vercel)
vercel --prod
URLs
text
Local:      http://localhost:3000
Backend:    https://your-app.up.railway.app
Frontend:   https://your-vercel-app.vercel.app
Health:     https://your-app.up.railway.app/health
QUEUE SOLVED - Making clinic queues smart, fast, and patient-friendly! 🏥🚀
---

