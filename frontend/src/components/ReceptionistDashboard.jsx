import VoiceAnnouncement from './VoiceAnnouncement';
import { FaSignOutAlt, FaTv, FaSearch, FaThLarge, FaChartLine } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import React, { useState, useEffect } from 'react';
import PatientForm from './PatientForm';
import QueueTable from './QueueTable';
import DoctorAnalytics from './DoctorAnalytics';
import { patientApi } from '../services/api';
import webSocketService from '../services/websocket';
import { toast } from 'react-toastify';
import '../styles/dashboard.css';
const handleCallNext = async () => {
  try {
    const response = await patientApi.callNext();
    await fetchQueue();
    toast.success('Called next patient');
    
    // Voice announcement
    if (response.data) {
      const patient = response.data;
      const isEmergency = patient.emergency || false;
      const message = isEmergency 
        ? `Emergency patient ${patient.token}, please proceed immediately.`
        : `Token Number ${patient.token}, ${patient.name}, please proceed to the consultation room.`;
      
      // Speak the message
      if (window.speechSynthesis) {
        const utterance = new SpeechSynthesisUtterance(message);
        utterance.lang = 'en-US';
        utterance.rate = 0.9;
        utterance.pitch = 1.1;
        utterance.volume = 1;
        window.speechSynthesis.speak(utterance);
        
        // Repeat after 2 seconds
        setTimeout(() => {
          window.speechSynthesis.speak(utterance);
        }, 2000);
      }
    }
  } catch (error) {
    console.error('Error calling next:', error);
    toast.error('Failed to call next patient');
  }
};

const ReceptionistDashboard = () => {
  const [queueData, setQueueData] = useState({
    currentlyConsulting: null,
    waitingList: [],
    totalWaiting: 0,
    completedToday: 0,
    totalPatients: 0
  });
  const [loading, setLoading] = useState(true);
  const [showAnalytics, setShowAnalytics] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('username');
    navigate('/login');
    toast.info('Logged out successfully');
  };

  const fetchQueue = async () => {
    try {
      setLoading(true);
      const response = await patientApi.getQueue();
      setQueueData(response.data);
    } catch (error) {
      console.error('Error fetching queue:', error);
      toast.error('Failed to fetch queue data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchQueue();

    webSocketService.connect((updatedQueue) => {
      console.log('Queue updated via WebSocket:', updatedQueue);
      setQueueData(updatedQueue);
      toast.info('Queue updated in real-time!');
    });

    return () => {
      webSocketService.disconnect();
    };
  }, []);

  const handlePatientAdded = async () => {
    await fetchQueue();
    toast.success('Patient added successfully!');
  };

  const handleCallNext = async () => {
  try {
    const response = await patientApi.callNext();
    await fetchQueue();
    toast.success('Called next patient');
    
    // Voice announcement
    if (response && response.data) {
      const patient = response.data;
      const isEmergency = patient.emergency || patient.isEmergency || false;
      
      // Create announcement message
      let message;
      if (isEmergency) {
        message = `Emergency patient ${patient.token}, please proceed immediately.`;
      } else {
        message = `Token Number ${patient.token}, ${patient.name}, please proceed to the consultation room.`;
      }
      
      // Speak the message using Web Speech API
      if (window.speechSynthesis) {
        // Cancel any ongoing speech
        window.speechSynthesis.cancel();
        
        const utterance = new SpeechSynthesisUtterance(message);
        utterance.lang = 'en-US';
        utterance.rate = 0.9;
        utterance.pitch = 1.1;
        utterance.volume = 1;
        
        // Speak twice
        window.speechSynthesis.speak(utterance);
        setTimeout(() => {
          window.speechSynthesis.speak(utterance);
        }, 2000);
        
        console.log('🔊 Announcement:', message);
      } else {
        console.warn('Speech synthesis not supported in this browser');
      }
    }
  } catch (error) {
    console.error('Error calling next:', error);
    toast.error('Failed to call next patient');
  }
};

  const handleSkipPatient = async (id) => {
    try {
      await patientApi.skipPatient(id);
      await fetchQueue();
      toast.success('Patient skipped');
    } catch (error) {
      console.error('Error skipping patient:', error);
      toast.error('Failed to skip patient');
    }
  };

  const handleCompletePatient = async (id) => {
    try {
      await patientApi.completePatient(id);
      await fetchQueue();
      toast.success('Patient consultation completed');
    } catch (error) {
      console.error('Error completing patient:', error);
      toast.error('Failed to complete patient');
    }
  };

  const handleDeletePatient = async (id) => {
    if (window.confirm('Are you sure you want to delete this patient?')) {
      try {
        await patientApi.deletePatient(id);
        await fetchQueue();
        toast.success('Patient deleted');
      } catch (error) {
        console.error('Error deleting patient:', error);
        toast.error('Failed to delete patient');
      }
    }
  };

  const handleSearchPatient = async (phone) => {
    try {
      const response = await patientApi.searchByPhone(phone);
      if (response.data.length > 0) {
        toast.info(`Found ${response.data.length} patient(s) with phone ${phone}`);
      } else {
        toast.warning('No patients found with this phone number');
      }
    } catch (error) {
      console.error('Error searching patient:', error);
      toast.error('Failed to search patients');
    }
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <VoiceAnnouncement />
        <h1>Receptionist Dashboard</h1>
        <div className="header-actions">
          <button className="btn-tv" onClick={() => window.open('/tv', '_blank')}>
            <FaTv /> TV Display
          </button>
          <button className="btn-analytics" onClick={() => setShowAnalytics(!showAnalytics)}>
            {showAnalytics ? 'Hide Analytics' : 'Show Analytics'}
          </button>
          <button className="btn-logout" onClick={handleLogout}>
            <FaSignOutAlt /> Logout
          </button>
        </div>
      </div>

      {showAnalytics && (
        <div className="analytics-section">
          <DoctorAnalytics />
        </div>
      )}

      <div className="dashboard-grid">
        <div className="dashboard-left">
          <PatientForm 
            onPatientAdded={handlePatientAdded}
            onSearchPatient={handleSearchPatient}
          />
        </div>

        <div className="dashboard-right">
          <div className="queue-stats">
            <div className="stat-card">
              <div className="stat-label">Waiting</div>
              <div className="stat-value">{queueData.totalWaiting}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Currently Consulting</div>
              <div className="stat-value">
                {queueData.currentlyConsulting ? 
                  queueData.currentlyConsulting.token : 'None'}
              </div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Completed Today</div>
              <div className="stat-value">{queueData.completedToday}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Total Patients</div>
              <div className="stat-value">{queueData.totalPatients}</div>
            </div>
          </div>

          {queueData.currentlyConsulting && (
            <div className="current-consulting-banner">
              <span className="banner-label">Currently Consulting:</span>
              <span className="banner-token">{queueData.currentlyConsulting.token}</span>
              <span className="banner-name">{queueData.currentlyConsulting.name}</span>
              <button 
                className="btn-complete-consulting"
                onClick={() => handleCompletePatient(queueData.currentlyConsulting.id)}
              >
                Complete Consultation
              </button>
            </div>
          )}

          <button 
            className="btn-call-next"
            onClick={handleCallNext}
            disabled={queueData.totalWaiting === 0}
          >
            {queueData.totalWaiting === 0 ? 
              'No patients waiting' : 
              `Call Next (${queueData.totalWaiting} waiting)`
            }
          </button>

          <QueueTable 
            waitingList={queueData.waitingList}
            onSkip={handleSkipPatient}
            onComplete={handleCompletePatient}
            onDelete={handleDeletePatient}
            loading={loading}
          />
        </div>
      </div>
    </div>
  );
};

export default ReceptionistDashboard;