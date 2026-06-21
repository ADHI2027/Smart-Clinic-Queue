# 🏥 SmartClinic Queue Management System

A production-ready clinic queue management system with real-time updates, dynamic ETA predictions, and ML-enhanced waiting time calculations.

![SmartClinic Queue](https://img.shields.io/badge/version-1.0.0-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)
![React](https://img.shields.io/badge/React-18-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green)

---

## 📋 Overview

SmartClinic is a **modern clinic queue management system** that eliminates paper tokens and provides real-time queue visibility. It features a **Receptionist Dashboard** and a **Waiting Room TV Display** with automatic real-time updates.

### 🎯 Key Features

- ✅ **Real-time Queue Management** - Add, call, skip, complete, or delete patients
- ✅ **Automatic Token Generation** - Sequential tokens (A001, A002, A003...)
- ✅ **Live TV Display** - Shows current consulting patient and upcoming tokens
- ✅ **WebSocket Updates** - Instant synchronization between all screens
- ✅ **Dynamic ETA Calculation** - Uses disease averages from database
- ✅ **ML-Enhanced Predictions** - Self-learning system that improves with data
- ✅ **Doctor Analytics** - Track total patients, completed, waiting, and averages
- ✅ **Secure Login** - Protected receptionist dashboard
- ✅ **Patient Search** - Search patients by phone number
- ✅ **Estimated End Time** - Shows when consultation will finish
- ✅ **Responsive Design** - Works on all screen sizes

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

text

### Database
MongoDB 6+

text

---

## 📁 Project Structure
smartclinic-queue/
├── backend/
│ ├── src/main/java/com/smartclinic/
│ │ ├── SmartClinicApplication.java
│ │ ├── config/
│ │ │ ├── CorsConfig.java
│ │ │ └── WebSocketConfig.java
│ │ ├── controller/
│ │ │ ├── PatientController.java
│ │ │ ├── ETAController.java
│ │ │ └── MLController.java
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
│ │ │ └── ETAResponse.java
│ │ └── exception/
│ │ ├── PatientNotFoundException.java
│ │ └── GlobalExceptionHandler.java
│ ├── src/main/resources/
│ │ └── application.properties
│ └── pom.xml
├── frontend/
│ ├── public/
│ │ └── index.html
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
│ │ │ └── DoctorAnalytics.jsx
│ │ ├── services/
│ │ │ ├── api.js
│ │ │ └── websocket.js
│ │ └── styles/
│ │ ├── dashboard.css
│ │ ├── tvdisplay.css
│ │ ├── queue.css
│ │ └── login.css
│ ├── package.json
│ └── .env
└── README.md

text

---

## 🗄️ Database Schema

### 1. patients
```javascript
{
  _id: ObjectId,
  token: "A001",                    // Auto-generated
  name: "Ram Kumar",
  phone: "9876543210",
  disease: "Fever",
  status: "WAITING",                // WAITING | CONSULTING | COMPLETED | SKIPPED
  consultationDuration: 8,          // In minutes (from disease average)
  estimatedTime: "10:45 AM",        // When patient will be called
  queuePosition: 1,
  createdAt: Date,
  // ML Fields
  age: 35,
  gender: "Male",
  doctor: "Dr. Arun",
  dayOfWeek: "Monday",
  timeSlot: "Morning"
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
5. ml_metadata
javascript
{
  _id: ObjectId,
  modelVersion: "v1.0",
  algorithm: "WeightedAverage",
  trainingDate: Date,
  trainingRecords: 150,
  accuracy: 0.85,
  mae: 3.5,
  isActive: true,
  featureWeights: {
    disease: 0.35,
    doctor: 0.25,
    queue: 0.25,
    time: 0.10,
    base: 0.05
  }
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
ETA & ML
Method	Endpoint	Description
GET	/api/eta/{token}	Get ETA for a patient
GET	/api/eta/statistics/disease	Get disease statistics
GET	/api/eta/statistics/doctor	Get doctor statistics
POST	/api/ml/train	Train ML model
GET	/api/ml/metrics	Get model metrics
GET	/api/ml/predict	Get ML prediction
WebSocket
Endpoint	Description
/ws	WebSocket endpoint
/topic/queue	Queue updates
🚀 Installation & Setup
Prerequisites
Java 21 or higher

Node.js 16+ and npm

MongoDB 6+

Maven 3.8+

Step 1: Clone the Repository
bash
git clone <repository-url>
cd smartclinic-queue
Step 2: MongoDB Setup
A. Start MongoDB Service
Windows:

bash
# As Administrator
net start MongoDB

# Or manual start
cd "C:\Program Files\MongoDB\Server\8.3\bin"
mongod.exe --dbpath "C:\data\db"
macOS:

bash
brew services start mongodb-community
Linux:

bash
sudo systemctl start mongod
B. Create Database & Collections
Using MongoDB Compass:

Connect to mongodb://localhost:27017

Create database: smartclinic_queue

Create collections:

patients

disease_statistics

doctor_statistics

consultation_history

ml_metadata

C. Insert Disease Data
Insert these documents into disease_statistics:

json
[
  {"disease": "Fever", "averageDuration": 8.5, "minDuration": 3, "maxDuration": 12, "totalPatients": 45},
  {"disease": "Cold", "averageDuration": 5.5, "minDuration": 3, "maxDuration": 8, "totalPatients": 32},
  {"disease": "Diabetes", "averageDuration": 18.5, "minDuration": 10, "maxDuration": 25, "totalPatients": 38},
  {"disease": "BP", "averageDuration": 10.5, "minDuration": 5, "maxDuration": 15, "totalPatients": 25},
  {"disease": "Migraine", "averageDuration": 15.0, "minDuration": 10, "maxDuration": 20, "totalPatients": 18},
  {"disease": "Pregnancy", "averageDuration": 25.5, "minDuration": 15, "maxDuration": 40, "totalPatients": 12},
  {"disease": "Emergency", "averageDuration": 30.0, "minDuration": 20, "maxDuration": 45, "totalPatients": 8}
]
D. Insert Doctor Data
Insert these documents into doctor_statistics:

json
[
  {"doctor": "Dr. Arun", "averageDuration": 12.5, "minDuration": 5, "maxDuration": 22, "totalPatients": 42},
  {"doctor": "Dr. Ravi", "averageDuration": 10.0, "minDuration": 4, "maxDuration": 18, "totalPatients": 35},
  {"doctor": "Dr. Priya", "averageDuration": 14.5, "minDuration": 8, "maxDuration": 25, "totalPatients": 28}
]
Step 3: Backend Setup
bash
cd backend
mvn clean install
mvn spring-boot:run
The backend will run on: http://localhost:8080

Step 4: Frontend Setup
bash
cd frontend
npm install
npm start
The frontend will run on: http://localhost:3000

🎮 How to Use
Login
text
URL: http://localhost:3000
Username: receptionist
Password: clinic123
Receptionist Dashboard
1. Add a Patient
Fill in: Name, Phone, Disease

Click "Add Patient"

System automatically calculates duration from disease average

Patient added to queue with auto-generated token (A001, A002...)

2. Manage Queue
Button	Action
Call Next	Moves first waiting patient to consulting
Skip	Skips a waiting patient
Complete	Completes consultation and automatically calls next
Delete	Removes patient from queue
3. Search Patients
Enter phone number

Click "Search"

View patient details

4. View Analytics
Click "Show Analytics"

View: Total Patients, Completed, Waiting, Average Time

5. TV Display
Click "TV Display" button

Opens TV display in new tab

TV Display (Waiting Room)
text
URL: http://localhost:3000/tv
Shows:

Currently Consulting - Token, Name, Disease, Start Time, End Time

Upcoming Tokens - List of waiting patients with their ETAs

Features:

🔄 Auto-updates in real-time (no refresh needed)

🖥️ Fullscreen mode available

🔢 Large fonts for easy viewing

⏰ Estimated End Time for each patient

🧠 How ETA Prediction Works
1. Disease-Based Prediction
text
Step 1: Patient selects disease "Fever"
Step 2: System queries disease_statistics
Step 3: Finds Fever average = 8.5 minutes
Step 4: Uses 8.5 minutes as consultation duration
2. Queue ETA Calculation
text
Current Time: 10:00 AM
Consulting: A001 (10 mins, started 09:50 AM, remaining 0 mins)
Queue:
  A002 (8.5 mins) → ETA: 10:00 + 8.5 = 10:08 AM
  A003 (18.5 mins) → ETA: 10:08 + 18.5 = 10:26 AM
  A004 (5.5 mins) → ETA: 10:26 + 5.5 = 10:31 AM
3. ML Enhancement
The system uses a Weighted Average model that learns from historical data:

text
ML Prediction = 0.35 × DiseaseAvg + 0.25 × DoctorAvg + 0.25 × QueueFactor + 0.10 × TimeFactor + 0.05 × Base

Weights automatically adjust as more data is collected
📊 Analytics Dashboard
Metric	Description
Total Patients	All patients registered today
Completed	Patients who have finished consultation
Waiting	Patients currently in queue
Average Time	Average consultation duration
🔄 Real-Time Updates (WebSocket)
text
1. Receptionist adds patient
2. Backend saves to MongoDB
3. Broadcasts update to WebSocket
4. TV Display updates instantly
5. Dashboard updates instantly
No page refresh needed!

🛠️ Troubleshooting
MongoDB Connection Issues
bash
# Check if MongoDB is running
net start MongoDB  # Windows
sudo systemctl status mongod  # Linux
brew services list  # macOS

# If port 27017 is in use
taskkill /IM mongod.exe /F  # Windows
sudo killall mongod  # Linux/macOS
Backend Won't Start
bash
# Check if port 8080 is in use
netstat -ano | findstr 8080  # Windows
lsof -i :8080  # macOS/Linux

# Rebuild
mvn clean compile
mvn spring-boot:run
Frontend Won't Start
bash
# Reinstall dependencies
rm -rf node_modules
npm install
npm start

# If port 3000 is in use
npm start -- --port 3001
Compilation Errors
bash
# Clean build
mvn clean compile

# Check for duplicate imports in React components
# Remove any duplicate import statements
🌟 Bonus Features
1. Disease Averages Auto-Update
System records every consultation

Averages recalculated daily at 2 AM

Predictions improve with more data

2. ML Model Training
Train model: POST /api/ml/train

Check metrics: GET /api/ml/metrics

Get prediction: GET /api/ml/predict?disease=Fever&queueLength=2

3. Search by Phone
Find patient history

Quick lookup for returning patients

4. Estimated End Time
Shows when consultation will finish

Helps patients plan their time

5. TV Display Button
One-click access from dashboard

Opens in new tab

📝 License
This project is licensed under the MIT License.

🤝 Contributing
Fork the repository

Create your feature branch: git checkout -b feature/AmazingFeature

Commit your changes: git commit -m 'Add some AmazingFeature'

Push to the branch: git push origin feature/AmazingFeature

Open a Pull Request

📧 Support
For support, email: support@smartclinic.com

🎉 Acknowledgments
Built with ❤️ using Spring Boot and React

Real-time updates powered by WebSocket

Smart predictions with ML

📌 Quick Reference
Commands Summary
bash
# Start MongoDB
cd "C:\Program Files\MongoDB\Server\8.3\bin" && mongod.exe --dbpath "C:\data\db"

# Start Backend
cd backend && mvn spring-boot:run

# Start Frontend
cd frontend && npm start

# Login
Username: receptionist
Password: clinic123

# Access
Dashboard: http://localhost:3000
TV Display: http://localhost:3000/tv
Default Disease Durations
Disease	Duration
Fever	8.5 mins
Cold	5.5 mins
Diabetes	18.5 mins
BP	10.5 mins
Migraine	15.0 mins
Pregnancy	25.5 mins
Emergency	30.0 mins
Made with ❤️ for clinics everywhere

text

---

This README is now complete and covers:
- ✅ Project overview and features
- ✅ Technology stack
- ✅ Complete folder structure
- ✅ Database schema
- ✅ API documentation
- ✅ Installation & setup guide
- ✅ How to use
- ✅ ETA prediction explanation
- ✅ Troubleshooting guide
- ✅ Quick reference

Save this as `README.md` in your project root! 🚀