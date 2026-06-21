import React from 'react';
import { FaClock, FaUsers, FaHourglassHalf } from 'react-icons/fa';
import '../styles/tvdisplay.css';

const UpcomingTokens = ({ waitingList, totalWaiting }) => {
  const getDisplayList = () => {
    return waitingList.slice(0, 10);
  };

  const formatTime = (timeString) => {
    if (!timeString) return 'Pending';
    return timeString;
  };

  const calculateEndTime = (estimatedTime, duration) => {
    if (!estimatedTime) return 'Pending';
    
    // Parse the estimated time (e.g., "08:35 AM")
    try {
      const [time, period] = estimatedTime.split(' ');
      const [hours, minutes] = time.split(':');
      let hour = parseInt(hours);
      const minute = parseInt(minutes);
      
      if (period === 'PM' && hour !== 12) hour += 12;
      if (period === 'AM' && hour === 12) hour = 0;
      
      const date = new Date();
      date.setHours(hour, minute, 0, 0);
      
      // Add duration
      const durationMinutes = duration || 10;
      date.setMinutes(date.getMinutes() + durationMinutes);
      
      return date.toLocaleTimeString('en-US', { 
        hour: '2-digit', 
        minute: '2-digit',
        hour12: true 
      });
    } catch (e) {
      return 'Pending';
    }
  };

  const displayList = getDisplayList();

  return (
    <div className="upcoming-tokens">
      <div className="upcoming-header">
        <div className="header-left">
          <FaUsers className="header-icon" />
          <h2>Upcoming Tokens</h2>
        </div>
        <div className="header-right">
          <span className="total-count">{totalWaiting} waiting</span>
        </div>
      </div>

      <div className="tokens-list">
        {displayList.length === 0 ? (
          <div className="empty-state">
            <p>No patients waiting</p>
          </div>
        ) : (
          displayList.map((patient, index) => {
            const endTime = calculateEndTime(patient.estimatedTime, patient.consultationDuration);
            
            return (
              <div key={patient.id} className="token-item">
                <div className="token-position">{index + 1}</div>
                <div className="token-number">{patient.token}</div>
                <div className="token-name">{patient.name}</div>
                <div className="token-time">
                  <FaClock className="time-icon" />
                  <span className="time-label">Starts:</span>
                  <span className="time-value">{formatTime(patient.estimatedTime)}</span>
                </div>
                {/* NEW: Estimated End Time */}
                <div className="token-end-time">
                  <FaHourglassHalf className="end-icon" />
                  <span className="end-label">Ends:</span>
                  <span className="end-value">{endTime}</span>
                </div>
              </div>
            );
          })
        )}
      </div>

      {totalWaiting > 10 && (
        <div className="more-indicator">
          +{totalWaiting - 10} more patients waiting
        </div>
      )}
    </div>
  );
};

export default UpcomingTokens;