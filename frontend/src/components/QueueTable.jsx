import React from 'react';
import { FaCheck, FaForward, FaTrash, FaClock } from 'react-icons/fa';
import '../styles/queue.css';

const QueueTable = ({ waitingList, onSkip, onComplete, onDelete, loading }) => {
  if (loading) {
    return (
      <div className="queue-table-container">
        <div className="loading-state">Loading queue...</div>
      </div>
    );
  }

  if (!waitingList || waitingList.length === 0) {
    return (
      <div className="queue-table-container">
        <div className="empty-state">
          <div className="empty-icon">📋</div>
          <h3>No patients in queue</h3>
          <p>Waiting for patients to be registered</p>
        </div>
      </div>
    );
  }

  return (
    <div className="queue-table-container">
      <div className="queue-table-header">
        <h3>Patient Queue</h3>
        <span className="queue-count">{waitingList.length} patients waiting</span>
      </div>
      
      <div className="table-wrapper">
        <table className="queue-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Token</th>
              <th>Name</th>
              <th>Disease</th>
              <th>Status</th>
              <th>Est. Time</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {waitingList.map((patient, index) => (
              <tr key={patient.id} className="queue-row">
                <td className="queue-position">{index + 1}</td>
                <td className="token-cell">
                  <span className="token-badge">{patient.token}</span>
                </td>
                <td className="name-cell">{patient.name}</td>
                <td className="disease-cell">{patient.disease}</td>
                <td className="status-cell">
                  <span className={`status-badge status-${patient.status.toLowerCase()}`}>
                    {patient.status}
                  </span>
                </td>
                <td className="time-cell">
                  <FaClock className="time-icon" />
                  {patient.estimatedTime || 'Pending'}
                </td>
                <td className="actions-cell">
                  <button
                    className="btn-action btn-complete"
                    onClick={() => onComplete(patient.id)}
                    title="Complete consultation"
                  >
                    <FaCheck />
                  </button>
                  <button
                    className="btn-action btn-skip"
                    onClick={() => onSkip(patient.id)}
                    title="Skip patient"
                  >
                    <FaForward />
                  </button>
                  <button
                    className="btn-action btn-delete"
                    onClick={() => onDelete(patient.id)}
                    title="Delete patient"
                  >
                    <FaTrash />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default QueueTable;