import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import '../styles/self-registration.css';

const SelfRegistration = () => {
  const [formData, setFormData] = useState({
    name: '',
    age: '',
    gender: 'Male',
    phone: '',
    symptoms: '',
    disease: ''
  });
  const [loading, setLoading] = useState(false);
  const [registrationComplete, setRegistrationComplete] = useState(false);
  const [response, setResponse] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.name || !formData.phone || !formData.disease) {
      toast.warning('Please fill in all required fields');
      return;
    }

    setLoading(true);
    try {
      const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
const res = await axios.post(`${API_URL}/api/self-register`, formData);
      setResponse(res.data);
      setRegistrationComplete(true);
      toast.success('Registration successful!');
    } catch (error) {
      console.error('Registration error:', error);
      toast.error('Failed to register. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleTrackQueue = () => {
    navigate('/tv');
  };

  if (registrationComplete && response) {
    const now = new Date();
    const formattedDate = now.toLocaleDateString('en-GB').replace(/\//g, '-') + ', ' + 
                          now.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit' });

    return (
      <div className="self-registration-container">
        <div className="mobile-frame-wrapper">
          <div className="mobile-notch"></div>
          <div className="mobile-status-bar">
            <span>9:41</span>
            <div className="mobile-status-icons">📶 🛜 🔋</div>
          </div>

          <div className="ticket-screen-body">
            
            {/* FlexQ Logo Block */}
            <div className="ticket-logo-block">
              <div className="ticket-logo-text">
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#00e676" strokeWidth="3.5" strokeLinecap="round" strokeLinejoin="round" style={{ marginRight: '6px' }}>
                  <polygon points="12 2 22 7 22 17 12 22 2 17 2 7 12 2"/>
                </svg>
                FlexQ
              </div>
              <span className="ticket-subtitle">Smart Clinic Enterprise</span>
            </div>

            {/* Ticket Coupon Card */}
            <div className="ticket-card">
              <span className="ticket-item-label" style={{ textTransform: 'uppercase', fontSize: '0.74rem', letterSpacing: '0.8px' }}>Your Token</span>
              <div className="ticket-token-number">{response.token}</div>
              
              <div className="ticket-divider"></div>

              <div className="ticket-info-list">
                <div className="ticket-info-item red">
                  <span className="ticket-item-label">Patients Ahead</span>
                  <span className="ticket-item-value">{response.patientsAhead}</span>
                </div>
                <div className="ticket-info-item blue">
                  <span className="ticket-item-label">Expected Wait</span>
                  <span className="ticket-item-value">
                    {response.expectedWaitMin} - {response.expectedWaitMax} min
                  </span>
                </div>
                <div className="ticket-info-item orange">
                  <span className="ticket-item-label">Estimated Time</span>
                  <span className="ticket-item-value">{response.estimatedTime}</span>
                </div>
              </div>

              <div className="ticket-timestamp">
                {formattedDate}
              </div>
            </div>

            {/* Actions group below card */}
            <div className="ticket-actions-group">
              <button className="ticket-primary-btn" onClick={handleTrackQueue}>
                Track Queue
              </button>
              <button 
                className="ticket-secondary-btn" 
                onClick={() => {
                  setRegistrationComplete(false);
                  setFormData({ name: '', age: '', gender: 'Male', phone: '', symptoms: '', disease: '' });
                  setResponse(null);
                }}
              >
                Register Another Patient
              </button>
            </div>

          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="self-registration-container">
      <div className="mobile-frame-wrapper">
        <div className="mobile-notch"></div>
        <div className="mobile-status-bar">
          <span>9:41</span>
          <div className="mobile-status-icons">📶 🛜 🔋</div>
        </div>

        <div className="self-registration-box">
          <h1>📋 Self Registration</h1>
          <p className="subtitle">Fill in the details below to book your appointment</p>

          <form onSubmit={handleSubmit} className="self-registration-form">
            <div className="form-group">
              <label>Patient Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                placeholder="Enter full name"
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Age</label>
                <input
                  type="number"
                  name="age"
                  value={formData.age}
                  onChange={handleChange}
                  placeholder="Age"
                />
              </div>
              <div className="form-group">
                <label>Gender</label>
                <select name="gender" value={formData.gender} onChange={handleChange}>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Phone Number *</label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                placeholder="Enter 10-digit phone number"
                maxLength="10"
                required
              />
            </div>

            <div className="form-group">
              <label>Symptoms</label>
              <textarea
                name="symptoms"
                value={formData.symptoms}
                onChange={handleChange}
                placeholder="Describe your symptoms"
                rows="3"
              />
            </div>

            <div className="form-group">
              <label>Disease / Consultation Reason *</label>
              <input
                type="text"
                name="disease"
                value={formData.disease}
                onChange={handleChange}
                placeholder="Enter disease or reason"
                required
              />
            </div>

            <button type="submit" className="book-appointment-btn" disabled={loading}>
              {loading ? 'Booking...' : 'Book Appointment'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SelfRegistration;