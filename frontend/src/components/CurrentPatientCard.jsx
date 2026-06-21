import React from 'react';
import { FaUserMd, FaStethoscope, FaClock, FaHourglassEnd } from 'react-icons/fa';
import '../styles/tvdisplay.css';

const CurrentPatientCard = ({ patient }) => {
  if (!patient) {
    return (
      <div className="current-patient-card empty">
        <div className="empty-state">
          <FaUserMd className="empty-icon" />
          <h2>No Patient Currently Consulting</h2>
          <p>Waiting for the next patient to be called</p>
        </div>
      </div>
    );
  }

  // Calculate end time
  const startTime = patient.createdAt ? new Date(patient.createdAt) : new Date();
  const duration = patient.consultationDuration || 10;
  const endTime = new Date(startTime.getTime() + duration * 60000);

  // Format time
  const formatTime = (date) => {
    return date.toLocaleTimeString('en-US', { 
      hour: '2-digit', 
      minute: '2-digit',
      hour12: true 
    });
  };

  return (
    <div className="current-patient-card">
      <div className="card-header">
        <FaStethoscope className="header-icon" />
        <span className="header-label">Currently Consulting</span>
      </div>
      
      <div className="patient-token">
        {patient.token}
      </div>
      
      <div className="patient-info">
        <div className="info-item">
          <span className="info-label">Patient Name</span>
          <span className="info-value">{patient.name}</span>
        </div>
        
        <div className="info-item">
          <span className="info-label">Consultation Duration</span>
          <span className="info-value">{patient.consultationDuration} minutes</span>
        </div>
        
        <div className="info-item">
          <span className="info-label">
            <FaClock className="info-icon" /> Started At
          </span>
          <span className="info-value">{formatTime(startTime)}</span>
        </div>
        
        {/* NEW: Estimated End Time */}
        <div className="info-item highlight">
          <span className="info-label">
            <FaHourglassEnd className="info-icon" /> Estimated End
          </span>
          <span className="info-value end-time">{formatTime(endTime)}</span>
        </div>
      </div>

      <div className="status-indicator consulting">
        <span className="pulse-dot"></span>
        In Consultation
      </div>
    </div>
  );
};

export default CurrentPatientCard;