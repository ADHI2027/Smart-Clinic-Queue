import React, { useState } from 'react';
import { patientApi } from '../services/api';
import { toast } from 'react-toastify';
import { FaSearch, FaUserPlus, FaTimes } from 'react-icons/fa';
import '../styles/dashboard.css';

const PatientForm = ({ onPatientAdded, onSearchPatient }) => {
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    disease: '',
    symptoms: ''
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [searchPhone, setSearchPhone] = useState('');

  // Emergency symptoms list
  const emergencySymptoms = [
    'chest pain',
    'breathing difficulty',
    'heavy bleeding',
    'stroke',
    'severe burns',
    'seizures',
    'high fever'
  ];

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Patient name is required';
    } else if (formData.name.trim().length < 2) {
      newErrors.name = 'Name must be at least 2 characters';
    }
    
    if (!formData.phone.trim()) {
      newErrors.phone = 'Phone number is required';
    } else if (!/^[0-9]{10}$/.test(formData.phone)) {
      newErrors.phone = 'Phone must be exactly 10 digits';
    }
    
    if (!formData.disease.trim()) {
      newErrors.disease = 'Disease/Reason is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      toast.error('Please fix the errors in the form');
      return;
    }

    // Check for emergency symptoms
    const symptoms = formData.symptoms?.toLowerCase() || '';
    const isEmergency = emergencySymptoms.some(s => symptoms.includes(s));

    let priorityApproved = false;
    if (isEmergency) {
      priorityApproved = window.confirm(
        '🚨 Emergency symptoms detected!\n\n' +
        'Patient: ' + formData.name + '\n' +
        'Symptoms: ' + formData.symptoms + '\n\n' +
        'Do you want to approve emergency priority?'
      );
      
      if (priorityApproved) {
        toast.success('🚨 Emergency priority approved!');
      } else {
        toast.info('Patient added to normal queue');
      }
    }

    setSubmitting(true);
    
    try {
      const patientData = {
        name: formData.name,
        phone: formData.phone,
        disease: formData.disease,
        symptoms: formData.symptoms || '',
        isEmergency: isEmergency,
        priorityApproved: priorityApproved,
        emergencyReason: isEmergency ? formData.disease : null
      };
      
      console.log('📤 Sending patient data:', patientData);
      
      const response = await patientApi.addPatient(patientData);
      
      console.log('✅ Patient added successfully:', response.data);
      
      toast.success(isEmergency && priorityApproved ? 
        '🚨 Emergency patient added with priority!' : 
        'Patient registered successfully!'
      );
      
      handleClear();
      if (onPatientAdded) {
        onPatientAdded();
      }
    } catch (error) {
      console.error('❌ Error adding patient:', error);
      console.error('❌ Error response:', error.response?.data);
      
      if (error.response?.data?.errors) {
        const errors = error.response.data.errors;
        const errorMessages = Object.values(errors).join(', ');
        toast.error('Validation error: ' + errorMessages);
      } else {
        toast.error(error.response?.data?.message || 'Failed to add patient. Please check the backend logs.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleClear = () => {
    setFormData({
      name: '',
      phone: '',
      disease: '',
      symptoms: ''
    });
    setErrors({});
  };

  const handleSearch = async () => {
    if (!searchPhone.trim()) {
      toast.warning('Please enter a phone number to search');
      return;
    }
    
    if (!/^[0-9]{10}$/.test(searchPhone)) {
      toast.warning('Please enter a valid 10-digit phone number');
      return;
    }
    
    if (onSearchPatient) {
      onSearchPatient(searchPhone);
    }
  };

  return (
    <div className="patient-form-container">
      <h2>Patient Registration</h2>
      
      <form onSubmit={handleSubmit} className="patient-form">
        <div className="form-group">
          <label htmlFor="name">Patient Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Enter patient name"
            className={errors.name ? 'error' : ''}
            disabled={submitting}
          />
          {errors.name && <span className="error-message">{errors.name}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="phone">Phone Number *</label>
          <input
            type="tel"
            id="phone"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            placeholder="Enter 10-digit phone number"
            className={errors.phone ? 'error' : ''}
            disabled={submitting}
            maxLength="10"
          />
          {errors.phone && <span className="error-message">{errors.phone}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="disease">Disease / Consultation Reason *</label>
          <input
            type="text"
            id="disease"
            name="disease"
            value={formData.disease}
            onChange={handleChange}
            placeholder="Enter disease or reason"
            className={errors.disease ? 'error' : ''}
            disabled={submitting}
          />
          {errors.disease && <span className="error-message">{errors.disease}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="symptoms">Symptoms</label>
          <textarea
            id="symptoms"
            name="symptoms"
            value={formData.symptoms}
            onChange={handleChange}
            placeholder="Describe symptoms (e.g., chest pain, breathing difficulty, heavy bleeding)"
            rows="2"
            className={errors.symptoms ? 'error' : ''}
            disabled={submitting}
          />
          <small className="helper-text">
            ⚠️ Emergency symptoms: chest pain, breathing difficulty, heavy bleeding, stroke, severe burns, seizures, high fever
          </small>
          {errors.symptoms && <span className="error-message">{errors.symptoms}</span>}
        </div>

        <div className="form-actions">
          <button 
            type="submit" 
            className="btn-add"
            disabled={submitting}
          >
            <FaUserPlus /> {submitting ? 'Adding...' : 'Add Patient'}
          </button>
          <button 
            type="button" 
            className="btn-clear"
            onClick={handleClear}
            disabled={submitting}
          >
            <FaTimes /> Clear Form
          </button>
        </div>
      </form>

      <div className="search-section">
        <h3>Search Patient</h3>
        <div className="search-container">
          <input
            type="tel"
            placeholder="Enter phone number to search"
            value={searchPhone}
            onChange={(e) => setSearchPhone(e.target.value)}
            maxLength="10"
          />
          <button onClick={handleSearch} className="btn-search">
            <FaSearch /> Search
          </button>
        </div>
      </div>
    </div>
  );
};

export default PatientForm;