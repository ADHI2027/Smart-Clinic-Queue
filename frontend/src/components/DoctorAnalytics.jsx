import React, { useState, useEffect } from 'react';
import { patientApi } from '../services/api';
import { 
  FaUsers, 
  FaCheckCircle, 
  FaClock, 
  FaHourglassHalf,
  FaFileExport 
} from 'react-icons/fa';
import { saveAs } from 'file-saver';
import '../styles/dashboard.css';

const DoctorAnalytics = () => {
  const [analytics, setAnalytics] = useState({
    totalPatients: 0,
    completed: 0,
    waiting: 0,
    consulting: 0,
    avgConsultationTime: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const queueResponse = await patientApi.getQueue();
      const queueData = queueResponse.data;
      
      // Calculate analytics
      setAnalytics({
        totalPatients: queueData.totalPatients || 0,
        completed: queueData.completedToday || 0,
        waiting: queueData.totalWaiting || 0,
        consulting: queueData.currentlyConsulting ? 1 : 0,
        avgConsultationTime: calculateAverageConsultationTime(queueData.waitingList || [])
      });
    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const calculateAverageConsultationTime = (waitingList) => {
    if (!waitingList || waitingList.length === 0) return 0;
    const total = waitingList.reduce((sum, patient) => 
      sum + (patient.consultationDuration || 10), 0
    );
    return Math.round(total / waitingList.length);
  };

  const exportToCSV = async () => {
    try {
      const response = await patientApi.getQueue();
      const queueData = response.data;
      const allPatients = [
        ...(queueData.currentlyConsulting ? [queueData.currentlyConsulting] : []),
        ...(queueData.waitingList || [])
      ];

      const csvContent = [
        ['Token', 'Name', 'Phone', 'Disease', 'Status', 'Duration', 'Estimated Time'],
        ...allPatients.map(p => [
          p.token,
          p.name,
          p.phone || 'N/A',
          p.disease,
          p.status,
          p.consultationDuration || 10,
          p.estimatedTime || 'Pending'
        ])
      ].map(row => row.join(',')).join('\n');

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
      saveAs(blob, `patients_${new Date().toISOString().split('T')[0]}.csv`);
    } catch (error) {
      console.error('Error exporting CSV:', error);
    }
  };

  const stats = [
    { 
      label: 'Total Patients', 
      value: analytics.totalPatients, 
      icon: FaUsers, 
      color: '#667eea',
      bgColor: '#f0f2ff'
    },
    { 
      label: 'Completed Today', 
      value: analytics.completed, 
      icon: FaCheckCircle, 
      color: '#27ae60',
      bgColor: '#e8f5e9'
    },
    { 
      label: 'Currently Waiting', 
      value: analytics.waiting, 
      icon: FaHourglassHalf, 
      color: '#f39c12',
      bgColor: '#fff3e0'
    },
    { 
      label: 'Avg. Consultation', 
      value: `${analytics.avgConsultationTime} min`, 
      icon: FaClock, 
      color: '#e74c3c',
      bgColor: '#fbe9e7'
    }
  ];

  if (loading) {
    return <div className="analytics-loading">Loading analytics...</div>;
  }

  return (
    <div className="analytics-container">
      <div className="analytics-header">
        <h2>Doctor Analytics</h2>
        <button onClick={exportToCSV} className="btn-export-glow">
          Export CSV
          <span className="arrow-badge">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" className="lucide lucide-arrow-up-right">
              <path d="M7 7h10v10"></path>
              <path d="M7 17 17 7"></path>
            </svg>
          </span>
        </button>
      </div>

      <div className="analytics-grid">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <div key={index} className="analytics-card">
              <div 
                className="analytics-icon"
                style={{ backgroundColor: stat.bgColor, color: stat.color }}
              >
                <Icon />
              </div>
              <div className="analytics-content">
                <div className="analytics-label">{stat.label}</div>
                <div className="analytics-value">{stat.value}</div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default DoctorAnalytics;