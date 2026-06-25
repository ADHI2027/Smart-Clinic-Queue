import React, { useState, useEffect } from 'react';
import { FaVolumeUp, FaVolumeMute } from 'react-icons/fa';

const VoiceAnnouncement = () => {
  const [isEnabled, setIsEnabled] = useState(true);
  const [lastAnnouncement, setLastAnnouncement] = useState('');

  useEffect(() => {
    // Listen for WebSocket events
    const handleAnnouncement = (event) => {
      if (!isEnabled) return;
      
      const message = event.detail;
      speakMessage(message);
    };

    window.addEventListener('announce', handleAnnouncement);
    return () => window.removeEventListener('announce', handleAnnouncement);
  }, [isEnabled]);

  const speakMessage = (message) => {
    if (!window.speechSynthesis) return;

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

    setLastAnnouncement(message);
  };

  const announceCallNext = (token, name) => {
    const message = `Token Number ${token}, ${name}, please proceed to the consultation room.`;
    window.dispatchEvent(new CustomEvent('announce', { detail: message }));
  };

  const announceEmergency = (token) => {
    const message = `Emergency patient ${token}, please proceed immediately.`;
    window.dispatchEvent(new CustomEvent('announce', { detail: message }));
  };

  const toggleAnnouncements = () => {
    setIsEnabled(!isEnabled);
  };

  return (
    <div className="voice-control">
      <div className="button-wrap">
        <button className="glass-button" onClick={toggleAnnouncements}>
          <span className="button-text">
            {isEnabled ? (
              <><FaVolumeUp /> Announcements ON</>
            ) : (
              <><FaVolumeMute /> Announcements OFF</>
            )}
          </span>
          <div className="button-shine"></div>
        </button>
      </div>
    </div>
  );
};

export default VoiceAnnouncement;