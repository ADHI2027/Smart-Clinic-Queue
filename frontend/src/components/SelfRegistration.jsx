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
      const res = await axios.post('http://localhost:8080/api/self-register', formData);
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
    return (
      <div className="self-registration-container">
        <div className="confirmation-box">
          <div className="success-icon">✅</div>
          <h1>Registration Complete!</h1>
          <div className="token-display">
            <span className="token-label">Your Token</span>
            <span className="token-number">{response.token}</span>
          </div>
          <div className="eta-display">
            <div className="eta-item">
              <span className="eta-label">Patients Ahead</span>
              <span className="eta-value">{response.patientsAhead}</span>
            </div>
            <div className="eta-item">
              <span className="eta-label">Expected Wait</span>
              <span className="eta-value">{response.expectedWaitMin} - {response.expectedWaitMax} mins</span>
            </div>
            <div className="eta-item">
              <span className="eta-label">Estimated Time</span>
              <span className="eta-value">{response.estimatedTime}</span>
            </div>
          </div>
          <button className="track-queue-btn" onClick={handleTrackQueue}>
            Track Queue
          </button>
          <button className="register-again-btn" onClick={() => {
            setRegistrationComplete(false);
            setFormData({ name: '', age: '', gender: 'Male', phone: '', symptoms: '', disease: '' });
            setResponse(null);
          }}>
            Register Another Patient
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="self-registration-container">
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
  );
};

export default SelfRegistration;