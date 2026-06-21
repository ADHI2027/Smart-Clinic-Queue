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
    consultationDuration: 10
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [searchPhone, setSearchPhone] = useState('');

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
    
    if (!formData.consultationDuration) {
      newErrors.consultationDuration = 'Consultation duration is required';
    } else if (formData.consultationDuration < 1 || formData.consultationDuration > 60) {
      newErrors.consultationDuration = 'Duration must be between 1-60 minutes';
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
    // Clear error for this field
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

    setSubmitting(true);
    try {
      await patientApi.addPatient(formData);
      toast.success('Patient registered successfully!');
      handleClear();
      if (onPatientAdded) {
        onPatientAdded();
      }
    } catch (error) {
      console.error('Error adding patient:', error);
      toast.error(error.response?.data?.message || 'Failed to add patient');
    } finally {
      setSubmitting(false);
    }
  };

  const handleClear = () => {
    setFormData({
      name: '',
      phone: '',
      disease: '',
      consultationDuration: 10
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

        {/*<div className="form-group">
          <label htmlFor="consultationDuration">Consultation Duration (minutes) *</label>
          <input
            type="number"
            id="consultationDuration"
            name="consultationDuration"
            value={formData.consultationDuration}
            onChange={handleChange}
            min="1"
            max="60"
            className={errors.consultationDuration ? 'error' : ''}
            disabled={submitting}
          />
          {errors.consultationDuration && (
            <span className="error-message">{errors.consultationDuration}</span>
          )}
        </div>*/}

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