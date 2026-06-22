import React, { useState, useEffect } from 'react';
import CurrentPatientCard from './CurrentPatientCard';
import UpcomingTokens from './UpcomingTokens';
import QRCode from 'qrcode.react';
import webSocketService from '../services/websocket';
import { patientApi } from '../services/api';
import '../styles/tvdisplay.css';

const TVDisplay = () => {
  const [queueData, setQueueData] = useState({
    currentlyConsulting: null,
    waitingList: [],
    totalWaiting: 0
  });
  const [isFullscreen, setIsFullscreen] = useState(false);

  // Get base URL from environment
  const baseUrl = process.env.REACT_APP_BASE_URL || window.location.origin;
  const qrValue = `${baseUrl}/self-register`;

  const fetchQueueData = async () => {
    try {
      const response = await patientApi.getQueue();
      setQueueData(response.data);
    } catch (error) {
      console.error('Error fetching queue data:', error);
    }
  };

  useEffect(() => {
    fetchQueueData();

    webSocketService.connect((updatedQueue) => {
      setQueueData(updatedQueue);
    });

    return () => {
      webSocketService.disconnect();
    };
  }, []);

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
      setIsFullscreen(true);
    } else {
      if (document.exitFullscreen) {
        document.exitFullscreen();
        setIsFullscreen(false);
      }
    }
  };

  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullscreen(!!document.fullscreenElement);
    };

    document.addEventListener('fullscreenchange', handleFullscreenChange);
    return () => {
      document.removeEventListener('fullscreenchange', handleFullscreenChange);
    };
  }, []);

  return (
    <div className="tv-display">
      <button 
        className="fullscreen-toggle"
        onClick={toggleFullscreen}
      >
        {isFullscreen ? 'Exit Fullscreen' : 'Fullscreen'}
      </button>

      <div className="tv-content">
        <header className="tv-header">
          <h1>QUEUE SOLVED</h1>
          <div className="qr-section">
            <QRCode value={qrValue} size={120} />
            <p className="qr-label">📱 Scan to Self Register</p>
          </div>
          <div className="tv-clock">
            {new Date().toLocaleTimeString('en-US', { 
              hour: '2-digit', 
              minute: '2-digit',
              hour12: true 
            })}
          </div>
        </header>

        <div className="tv-main">
          <div className="tv-left">
            <CurrentPatientCard 
              patient={queueData.currentlyConsulting}
            />
          </div>
          
          <div className="tv-right">
            <UpcomingTokens 
              waitingList={queueData.waitingList}
              totalWaiting={queueData.totalWaiting}
            />
          </div>
        </div>

        <footer className="tv-footer">
          <div className="footer-message">
            Please wait for your turn. Thank you for your patience! 🙏
          </div>
          <div className="footer-info">
            Total patients in queue: {queueData.totalWaiting}
          </div>
        </footer>
      </div>
    </div>
  );
};

export default TVDisplay;